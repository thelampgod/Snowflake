package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.packets.impl.outgoing.ConnectionPacket;
import com.google.common.collect.Sets;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

import static com.github.thelampgod.snowflake.util.Helper.*;

public class SnowflakeServer {
    private final String password;
    private ServerSocket server;

    public final Set<SocketClient> connectedClients = Sets.newConcurrentHashSet();
    public final Set<ClientHandler> threads = Sets.newConcurrentHashSet();

    public SnowflakeServer(String password) {
        this.password = password;
    }

    public void start(int port) {
        try {
            server = new ServerSocket(port);
            while (true) {
                ClientHandler thread = new ClientHandler(server.accept(), password);
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
        getLog().debug(client.toString() + " connected.");
    }

    public void removeClient(SocketClient client) {
        connectedClients.stream()
                .filter(c -> c.getLinkString() == null || c.getLinkString().equals(client.getLinkString()))
                .forEach(c -> {
                    connectedClients.remove(c);
                    getLog().debug(c + " disconnected.");
                    try {
                        c.getSocket().close();
                    } catch (IOException ignored) {
                    }
                });
        threads.stream()
                .filter(th -> th.client.getLinkString() == null || th.client.getLinkString().equals(client.getLinkString()))
                .forEach(th -> {
                    th.isRunning = false;
                    threads.remove(th);
                });

        getLog().info(client + " disconnected.");

        try {
            if (!client.isAuthenticated() || client.isReceiver()) return;
            sendDisconnectMessage(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDisconnectMessage(SocketClient client) throws IOException {
        for (SocketClient receiver : getConnectedClients()) {
            if (!receiver.isReceiver()) continue;

            receiver.getConnection().sendPacket(new ConnectionPacket.Disconnect(client.getId(), client.getName()));
        }

    }

    public SocketClient getClientReceiver(int clientId) {
        return this.connectedClients.stream()
                .filter(client -> client.getId() == clientId)
                .filter(SocketClient::isReceiver)
                .findAny()
                .orElse(null);
    }

    public SocketClient getClientReceiver(String linker) {
        return this.connectedClients.stream()
                .filter(client -> client.getLinkString().equals(linker))
                .filter(SocketClient::isReceiver)
                .findAny()
                .orElse(null);
    }
}
