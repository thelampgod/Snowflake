package com.github.thelampgod.snowflake;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.HashSet;

import static net.daporkchop.lib.logging.Logging.*;

public class SnowflakeServer {
    private ServerSocket server;

    private final HashSet<Socket> connectedClients = new HashSet<>();

    public void start(int port) {
        try {
            server = new ServerSocket(port);
            while (true) {
                new ClientHandler(server.accept()).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addClient(Socket s) {
        this.connectedClients.add(s);
        logger.debug(s.toString() + " connected.");
    }

    public void removeClient(Socket s) {
        this.connectedClients.remove(s);
        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(s + " disconnected.");
    }

    private static class ClientHandler extends Thread {
        private Socket client;
        private DataOutputStream out;
        private DataInputStream in;

        private enum Action {
            LOGIN(0),
            SEND_POSITION(1),
            ADD_USER(2);

            public final byte action;

            Action(int action) {
                this.action = (byte) action;
            }
        }

        public ClientHandler(Socket socket) {
            this.client = socket;
            Snowflake.INSTANCE.getServer().addClient(socket);
        }

        @Override
        public void run() {
            try {
                out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
                in = new DataInputStream(new BufferedInputStream(client.getInputStream()));

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


                Snowflake.INSTANCE.getServer().removeClient(client);
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                Snowflake.INSTANCE.getServer().removeClient(client);
            }
        }

        private void doAction(byte action) throws IOException {
            switch (parseAction(action)) {
                case LOGIN:
//                    String pubKey = in.readUTF();
//                    String secret = RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
//                    out.writeUTF(encrypt(secret, pubKey));
//                    out.flush();
//
//                    if (in.readUTF().equals(secret)) {
//                        //authenticated
//                    }
                    break;
                case SEND_POSITION:
                    checkAuth(client);

                    //read encrypted message and forward it to client's recipients
//                    String position = in.readUTF();
//                    for (Recipient r : recipients) {
//                        r.writeUTF(position);
//                    }

                    break;
                case ADD_USER:
                    checkAuth(client);

                    //read public key that client sends and store in recipient table and add to recipient table

                    break;
            }

        }

        private void checkAuth(Socket client) throws IOException {
            if (!(isAuthenticated(client))) {
                out.writeUTF("Not authenticated");
                out.flush();
                Snowflake.INSTANCE.getServer().removeClient(client);
            }
        }

        private boolean isAuthenticated(Socket client) {
            return false;
        }

        private Action parseAction(byte action) {
            switch (action) {
                case 1: return Action.SEND_POSITION;
                case 2: return Action.ADD_USER;

                default: return Action.LOGIN;
            }
        }
    }
}
