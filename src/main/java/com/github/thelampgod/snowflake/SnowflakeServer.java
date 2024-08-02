package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.impl.DisconnectPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.ConnectionPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupConnectionPacket;
import com.google.common.collect.Maps;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

import static com.github.thelampgod.snowflake.util.Helper.*;

public class SnowflakeServer {
    private final String password;
    private ServerSocket server;


    private final Map<String, ConnectionPair> connections = Maps.newConcurrentMap();

    public SnowflakeServer(String password) {
        this.password = password;
    }

    public void start(int port) {
        try {
            server = new ServerSocket(port);
            while (true) {
                ClientHandler thread = new ClientHandler(server.accept(), password);
                thread.start();
            }
        } catch (IOException ignored) {
        }
    }

    public void stop() throws IOException {
        for (ConnectionPair pair : connections.values()) {
            removeClient(pair);
        }
        server.close();
    }

    public void addClient(boolean receiver, ClientHandler thread) {
        final SocketClient client = thread.client;
        ConnectionPair pair = connections.computeIfAbsent(client.getLinkString(), secret -> {
            ConnectionPair p = new ConnectionPair(secret);
            if (receiver) {
                p.setReceiver(thread);
            } else {
                p.setTalker(thread);
            }
            return p;
        });

        if (receiver) {
            pair.setReceiver(thread);
        } else {
            pair.setTalker(thread);
        }

        getLog().debug(client + " connected.");
    }

    private void removeClient(ConnectionPair pair) {
        if (!connections.containsKey(pair.getSecret())) return;
        pair.getReceiver().sendPacketInstant(new DisconnectPacket("Disconnected"));
        pair.close();
        connections.remove(pair.getSecret());

        if (pair.isPaired()) {
            SocketClient receiver = pair.getReceiver().client;
            getLog().info(receiver + " disconnected.");
            if (!receiver.isAuthenticated()) return;

            this.sendDisconnectMessage(receiver);
        }
    }

    public void removeClient(SocketClient client) throws IOException {
        final ConnectionPair pair = connections.get(client.getLinkString());
        if (pair == null) return;
        this.removeClient(pair);
    }

    public void removeClient(ClientHandler connection) {
        final ConnectionPair pair = connections.get(connection.client.getLinkString());
        if (pair == null) return;
        this.removeClient(pair);
    }

    private void sendDisconnectMessage(SocketClient client) {
        for (Group group : Snowflake.INSTANCE.getGroupManager().findUserGroups(client.getId())) {
            for (int id : group.getUsers()) {
                ClientHandler user = this.getClientReceiver(id);
                if (user == null) continue;
                user.sendPacket(new GroupConnectionPacket.Left(group.getId(), client.getId()));
            }
        }

        for (ConnectionPair pair : connections.values()) {
            if (!pair.getReceiver().client.isAuthenticated()) return;
            pair.getReceiver().sendPacket(new ConnectionPacket.Disconnect(client.getId(), client.getName()));
        }
    }

    public ClientHandler getClientReceiver(int id) {
        for (ConnectionPair pair : connections.values()) {
            if (!pair.isPaired()) continue;
            ClientHandler receiver = pair.getReceiver();
            if (receiver.client.getId() == id) {
                return receiver;
            }
        }
        return null;
    }

    public ClientHandler getClientReceiver(ClientHandler connection) {
        return this.getClientReceiver(connection.client);
    }

    public ClientHandler getClientReceiver(SocketClient client) {
        return this.getClientReceiver(client.getLinkString());
    }

    public ClientHandler getClientReceiver(String linker) {
        return connections.get(linker).getReceiver();
    }

    public Collection<ConnectionPair> getConnections() {
        return connections.values();
    }

    public ConnectionPair get(SocketClient client) {
        return connections.get(client.getLinkString());
    }
}
