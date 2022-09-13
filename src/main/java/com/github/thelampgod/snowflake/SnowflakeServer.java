package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.util.DatabaseUtil;
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
            Snowflake.INSTANCE.getServer().addClient(this.client);
        }

        @Override
        public void run() {
            try {
                out = client.getOutputStream();
                in = client.getInputStream();

                out.writeUTF("Hello client");
                out.flush();

                while (true) {
                    try {
                        byte action = in.readByte();
                        this.doAction(action);
                    } catch (EOFException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                Snowflake.INSTANCE.getServer().removeClient(client);
            }
        }

        private void doAction(byte action) throws IOException {
            switch (action) {
                case 0:
                    login();
                    break;
                case 1:
                    sendPos();
                    break;
                case 2:
                    addRecipient();
                    break;
                case 3:
                    disconnect("Disconnected");
                    break;
            }

        }

        private List<Integer> getRecipientsFromDatabase(int id) {
            List<Integer> temp = new ArrayList<>();
            try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
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
            Snowflake.INSTANCE.getServer().removeClient(client);
        }

        private void checkAuth(SocketClient client) throws IOException {
            if (!client.isAuthenticated()) {
                disconnect("Not authenticated");
            }
        }

        private void login() throws IOException {
            String pubKey = in.readUTF();
            PGPPublicKeyRing key = PGPainless.readKeyRing().publicKeyRing(pubKey);
            String secret =
                    RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());

            String encryptedMessage = encrypt(secret, key);
            if (encryptedMessage.isEmpty())  {
                disconnect("Encryption fail (invalid key)");
                return;
            }

            out.writeUTF(encryptedMessage);
            out.flush();

            if (in.readUTF().equals(secret)) {
                client.setPubKey(pubKey);
                client.setName(new KeyRingInfo(key).getPrimaryUserId());
                int id = DatabaseUtil.insertUser(client);
                client.setId(id);
                this.recipientsIds.addAll(getRecipientsFromDatabase(id));

                out.writeUTF("Authenticated.");
                out.flush();

                logger.info(client + " authenticated.");
            } else {
                disconnect("Wrong password");
            }
        }

        private void sendPos() throws IOException {
            checkAuth(client);
            //user should add some recipients
            if (recipientsIds.isEmpty()) return;

            // read encrypted message and forward it to client's recipients
            String position = in.readUTF();

            Snowflake.INSTANCE.getServer().connectedClients.stream()
                    .filter(c -> recipientsIds.contains(c.getId()))
                    .forEach(c -> {
                        try {
                            DataOutputStream clientOut = c.getOutputStream();
                            // tell client it is packet type 1 => position update
                            clientOut.writeByte(1);
                            clientOut.writeUTF(position);
                            clientOut.flush();

                            logger.debug(client.getName() + " sent position packet to recipient " + c.getName());
                            out.writeUTF(client.getName() + " sent position packet to recipient " + c.getName());
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

        private void addRecipient() throws IOException {
            checkAuth(client);
            //read public key that client sends and store in recipient table and add to recipient table
            String recipientKey = in.readUTF();

            //check connected clients first
            Optional<SocketClient> optRecipient = Snowflake.INSTANCE.getServer().connectedClients.stream()
                    .filter(c -> c.getPubKey().equals(recipientKey))
                    .findAny();

            if (optRecipient.isPresent()) {
                SocketClient recipient = optRecipient.get();
                DatabaseUtil.insertRecipient(recipient.getId(), recipientKey, client.getId());
                out.writeUTF("added recipient successfully");
                out.flush();
                return;
            }

            //not connected, check database
            try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
                ResultSet result =
                        DatabaseUtil.runQuery(
                                "select id from users where pubkey=\"" + recipientKey + "\"", conn).getResultSet();
                if (result.next()) {
                    int userId = result.getInt("id");
                    result.close();

                    DatabaseUtil.insertRecipient(userId, recipientKey, client.getId());
                    out.writeUTF("added recipient successfully");
                    out.flush();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
