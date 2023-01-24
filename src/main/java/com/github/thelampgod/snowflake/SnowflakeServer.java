package com.github.thelampgod.snowflake;

import com.google.common.collect.Sets;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import static com.github.thelampgod.snowflake.util.Helper.*;
import static net.daporkchop.lib.logging.Logging.*;

public class SnowflakeServer {
    private ServerSocket server;

    public final Set<SocketClient> connectedClients = Sets.newConcurrentHashSet();
    public final Set<ClientHandler> threads = Sets.newConcurrentHashSet();

    public void start(int port) {
        try {
            server = new ServerSocket(port);
            while (true) {
                ClientHandler thread = new ClientHandler(server.accept());
                threads.add(thread);
                thread.start();
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
        connectedClients.stream()
                .filter(c -> c.getLinkString() == null || c.getLinkString().equals(client.getLinkString()))
                .forEach(c -> {
                    connectedClients.remove(c);
                    logger.debug(c + " disconnected.");
                    try {
                        c.getSocket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        threads.stream()
                .filter(th -> th.client.getLinkString() == null || th.client.getLinkString().equals(client.getLinkString()))
                .forEach(th -> {
                    th.isRunning = false;
                    threads.remove(th);
                });

        logger.info(client + " disconnected.");

        try {
            sendDisconnectMessage(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDisconnectMessage(SocketClient client) throws IOException {
        HashSet<Integer> sentTo = Sets.newHashSet();
        for (SocketClient receiver : getConnectedClients()) {
            if (!receiver.isReceiver() || sentTo.contains(receiver.getId())) continue;
            sentTo.add(receiver.getId());

            DataOutputStream out = receiver.getOutputStream();
            out.writeByte(6); //disconnect packet id
            out.writeInt(client.getId());
            out.writeUTF(client.getName());
            out.flush();
        }

    }
}
