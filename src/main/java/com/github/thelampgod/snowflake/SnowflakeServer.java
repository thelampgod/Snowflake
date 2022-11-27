package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.util.DatabaseUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.pgpainless.PGPainless;
import org.pgpainless.key.info.KeyRingInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.github.thelampgod.snowflake.util.EncryptionUtil.encrypt;
import static com.github.thelampgod.snowflake.util.Helper.*;
import static net.daporkchop.lib.logging.Logging.*;

public class SnowflakeServer {
    private ServerSocket server;

    public final HashSet<SocketClient> connectedClients = new HashSet<>();

    public void start(int port) {
        try {
            server = new ServerSocket(port);
            while (true) {
                new ClientHandler(server.accept()).start();
            }
        } catch (IOException ignored) {
        }
    }

    public void stop() {
        try {
            for (SocketClient client : connectedClients) {
                this.removeClient(client);
            }
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addClient(SocketClient client) {
        this.connectedClients.add(client);
        logger.debug(client.toString() + " connected.");
    }

    public void removeClient(SocketClient client) {
        if (!(connectedClients.contains(client))) return;
        this.connectedClients.remove(client);
        try {
            client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(client + " disconnected.");
    }

    private static class ClientHandler extends Thread {
        private final SocketClient client;
        private DataOutputStream out;
        private DataInputStream in;

        private final HashSet<Integer> recipientsIds = new HashSet<>();

        public ClientHandler(Socket socket) throws IOException {
            this.client = new SocketClient(socket);
            getServer().addClient(this.client);
        }

        @Override
        public void run() {
            try {
                out = client.getOutputStream();
                in = client.getInputStream();

                boolean receiver = in.readBoolean();
                String secret = in.readUTF();
                client.setLinker(secret);

                if (receiver) {
                    client.setReceiver(true);
                    logger.debug("Receiver connected " + client);
                    // keep the thread alive, so it is ready to send packets
                    while (true) {
                    }
                }

                logger.debug("Talker connected " + client);
                while (true) {
                    try {
                        byte action = in.readByte();
                        this.doAction(action);
                    } catch (EOFException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                getServer().removeClient(client);
            }
        }

        private void doAction(byte action) throws IOException {
            logger.debug(client + " action=" + action);

            switch (action) {
                case 0:
                    login();
                    break;
                case 1:
                    sendData();
                    break;
                case 2:
                    addRecipient();
                    break;
                case 3:
                    removeRecipient();
                    break;
                case 4:
                    getConnectedUsers();
                    break;
                case 5:
                    getRecipients();
                    break;
                case 6:
                    getKeyForId(in.readByte());
                    break;
                default:
                    disconnect("Disconnected");
                    break;
            }
        }

        private List<Integer> getRecipientsFromDatabase(int id) {
            List<Integer> temp = new ArrayList<>();
            try (Connection conn = getDb().getConnection()) {
                ResultSet result = DatabaseUtil.runQuery("select recipient_user_id from recipients where user_id=" + id, conn).getResultSet();
                while (result.next()) {
                    temp.add(result.getInt("recipient_user_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return temp;
        }

        private void disconnect(String reason) throws IOException {
            out.writeUTF(reason);
            out.flush();
            logger.info(client.toString() + " disconnected, reason: " + reason);
            getServer().removeClient(client);
        }

        private void checkAuth(SocketClient client) throws IOException {
            if (!client.isAuthenticated()) {
                disconnect("Not authenticated");
            }
        }

        private void login() throws IOException {
            //dont login twice
            if (client.isAuthenticated()) return;

            String pubKey = in.readUTF();
            PGPPublicKeyRing key = PGPainless.readKeyRing().publicKeyRing(pubKey);
            String secret =
                    RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());

            logger.debug("Generated `" + secret + "` as the secret password. Client needs to respond with this.");
            byte[] encryptedMessage = encrypt(secret, key);
            if (encryptedMessage.length < 1) {
                disconnect("Encryption fail (invalid key)");
                return;
            }

            out.writeInt(encryptedMessage.length);
            out.write(encryptedMessage);
            out.flush();

            if (in.readUTF().equals(secret)) {
                String name = new KeyRingInfo(key).getPrimaryUserId();

                client.setPubKey(pubKey);
                client.setName(name);
                int id = DatabaseUtil.insertUser(client);
                client.setId(id);
                this.recipientsIds.addAll(getRecipientsFromDatabase(id));

                getConnectedClients().stream()
                        .filter(c -> c.getLinkString().equals(client.getLinkString()))
                        .filter(SocketClient::isReceiver)
                        .forEach(c -> {
                            c.setPubKey(pubKey);
                            c.setName(name);
                            c.setId(id);
                            logger.info(c + " authenticated.");
                        });

                logger.info(client + " authenticated.");
            } else {
                disconnect("Wrong password");
            }
        }

        private void sendData() throws IOException {
            checkAuth(client);
            //user should add some recipients
            if (recipientsIds.isEmpty()) {
                logger.debug(client + " has no recipients.");
                return;
            }

            // read encrypted message and forward it to client's recipients
            String data = in.readUTF();

            getConnectedClients().stream()
                    .filter(c -> recipientsIds.contains(c.getId()))
                    .filter(SocketClient::isReceiver)
                    .forEach(c -> {
                        try {
                            DataOutputStream clientOut = c.getOutputStream();
                            // tell client it is packet type 1 => data update
                            clientOut.writeByte(1);
                            clientOut.writeUTF(data);
                            clientOut.flush();

                            logger.debug(client.getName() + " sent data packet to recipient " + c.getName());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        private void addRecipient() throws IOException {
            checkAuth(client);
            //add via id
            if (in.readByte() == 0) {
                int id = in.readInt();

                if (addViaId(id)) {
                    out.writeUTF("Added recipient successfully");
                    out.flush();
                } else {
                    out.writeUTF("Failed adding recipient");
                    out.flush();
                }
                return;
            }

            //else add via public key
            String recipientKey = in.readUTF();
            if (addViaKey(recipientKey)) {
                out.writeUTF("Added recipient successfully");
                out.flush();
            } else {
                out.writeUTF("Failed adding recipient");
                out.flush();
            }
        }

        private boolean addViaId(int id) {
            //check connected clients first
            Optional<SocketClient> optRecipient = getConnectedClients().stream()
                    .filter(c -> c.getId() == id)
                    .findAny();

            if (optRecipient.isPresent()) {
                SocketClient recipient = optRecipient.get();
                DatabaseUtil.insertRecipient(recipient.getId(), recipient.getPubKey(), client.getId());
                recipientsIds.add(recipient.getId());
                return true;
            }
            //not connected, check database
            try (Connection conn = getDb().getConnection()) {
                ResultSet result =
                        DatabaseUtil.runQuery(
                                "select pubkey from users where id=\"" + id + "\"", conn).getResultSet();
                if (result.next()) {
                    String pubKey = result.getString("pubkey");
                    result.close();

                    DatabaseUtil.insertRecipient(id, pubKey, client.getId());
                    recipientsIds.add(id);
                    return true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        private boolean addViaKey(String recipientKey) {
            //check connected clients first
            Optional<SocketClient> optRecipient = getConnectedClients().stream()
                    .filter(c -> c.getPubKey().equals(recipientKey))
                    .findAny();

            if (optRecipient.isPresent()) {
                SocketClient recipient = optRecipient.get();
                DatabaseUtil.insertRecipient(recipient.getId(), recipient.getPubKey(), client.getId());
                recipientsIds.add(recipient.getId());
                return true;
            }

            //not connected, check database
            try (Connection conn = getDb().getConnection()) {
                ResultSet result =
                        DatabaseUtil.runQuery(
                                "select id from users where pubkey=\"" + recipientKey + "\"", conn).getResultSet();
                if (result.next()) {
                    int userId = result.getInt("id");
                    result.close();

                    DatabaseUtil.insertRecipient(userId, recipientKey, client.getId());
                    recipientsIds.add(userId);
                    return true;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        private void removeRecipient() throws IOException {
            checkAuth(client);

            // remove via id
            if (in.readByte() == 0) {

                int id = in.readInt();
                DatabaseUtil.removeRecipient(id, client.getId());
                recipientsIds.remove(id);

                out.writeUTF("Removed recipient successfully");
                out.flush();
                return;
            }

            //else remove via public key
            String key = in.readUTF();
            Optional<Integer> id = DatabaseUtil.removeRecipient(key, client.getId());

            if (id.isPresent()) {
                recipientsIds.remove(id.get());
                out.writeUTF("Removed recipient successfully");
                out.flush();
            }
        }

        private void getConnectedUsers() throws IOException {
            checkAuth(client);
            JsonObject node = new JsonObject();
            for (SocketClient client : getConnectedClients()) {
                JsonObject jsonClient = new JsonObject();
                jsonClient.addProperty(String.valueOf(client.getId()), client.getName());
                jsonClient.entrySet().forEach(entry -> node.add(entry.getKey(), entry.getValue()));
            }

            out.writeUTF(new GsonBuilder().setPrettyPrinting().create().toJson(node));
            out.flush();
        }

        private void getRecipients() throws IOException {
            checkAuth(client);

            if (recipientsIds.isEmpty()) {
                out.writeUTF("No recipients.");
                out.flush();
                return;
            }

            StringBuilder b = new StringBuilder();
            for (int id : recipientsIds) {
                b.append(id).append(", ");
            }

            out.writeUTF(b.substring(0, b.length() - 2));
            out.flush();
        }

        private void getKeyForId(byte id) throws IOException {
            checkAuth(client);

            Optional<SocketClient> user = getConnectedClients().stream().filter(c -> c.getId() == id).findAny();

            if (user.isPresent()) {
                out.writeUTF(user.get().getPubKey());
                out.flush();
            }
        }
    }
}
