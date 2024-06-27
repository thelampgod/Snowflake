package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.KeepAlivePacket;
import com.github.thelampgod.snowflake.packets.impl.DisconnectPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.MultiPacketPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.github.thelampgod.snowflake.util.Helper.*;

public class ClientHandler extends Thread {
    final SocketClient client;
    private final String password;

    private static final String PROTOCOL_VERSION = "hNnRaVtqtNGECREZVhSNUbCwmcUjVlOZ";
    public boolean isRunning = true;
    private DataOutputStream out;
    private DataInputStream in;
    private final Queue<SnowflakePacket> outboundPacketsQueue = new ConcurrentLinkedQueue<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ClientHandler(Socket socket, String password) throws IOException {
        this.client = new SocketClient(socket, this);
        this.password = password;
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

            if (!receiver) {
                if (!in.readUTF().equals(PROTOCOL_VERSION)) {
                    this.sendPacket(new DisconnectPacket("Outdated client or server", client));
                    return;
                }
                if (!in.readUTF().equals(password) && !password.isEmpty()) {
                    this.sendPacket(new DisconnectPacket("Wrong password", client));
                    return;
                }
            }

            if (receiver) {
                runSendTick();
                client.setReceiver(true);
                getLog().debug("Receiver connected " + client);
                // keep the thread alive, so it is ready to send packets
                while (isRunning) {
                    sendKeepAlive();
                    Thread.sleep(15000);
                }
                return;
            }

            getLog().info(client + " connected.");
            getLog().debug("Talker connected " + client);
            while (isRunning) {
                SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in, client);
                getLog().debug("Received a " + packet.getClass().getSimpleName() + " from " + client);
                packet.handle();
            }
        } catch (Throwable ignored) {
            getServer().removeClient(client);
        }
    }

    private void runSendTick() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    flushOutboundQueue();

                    Thread.sleep(25); // 40 tps
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void flushOutboundQueue() throws IOException {
        boolean multiPacket = outboundPacketsQueue.size() > 2;
        readWriteLock.readLock().lock();

        try {
            if (multiPacket) {
                this.dispatchPacket(new MultiPacketPacket(outboundPacketsQueue));
            } else {

                while (!outboundPacketsQueue.isEmpty()) {
                    SnowflakePacket packet = outboundPacketsQueue.poll();
                    this.dispatchPacket(packet);
                }
            }
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    private void dispatchPacket(SnowflakePacket packet) throws IOException {
        getLog().debug("Sending a " + packet.getClass().getSimpleName() + " to " + client);
        packet.writeData(out);
        out.flush();

        if (packet instanceof DisconnectPacket disconnect) {
            getServer().removeClient(disconnect.getSender());
            getLog().debug(disconnect.getSender() + " disconnected. Reason: " + disconnect.getReason());
        }
    }

    public void sendPacket(SnowflakePacket packet) throws IOException {
        if (!this.client.isReceiver()) {
            SocketClient receiver = Snowflake.INSTANCE.getServer().getClientReceiver(this.client.getId());
            if (!this.client.isAuthenticated()) {
                receiver = Snowflake.INSTANCE.getServer().getClientReceiver(this.client.getLinkString());
            }
            if (receiver == null) return;
            receiver.getConnection().sendPacket(packet);
            return;
        }

        readWriteLock.writeLock().lock();

        try {
            outboundPacketsQueue.add(packet);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private void sendKeepAlive() throws IOException {
        if (!client.responded) {
            this.sendPacket(new DisconnectPacket("Timed out.", client));
            return;
        }
        client.responded = false;
        client.setNow(System.currentTimeMillis());
        this.dispatchPacket(new KeepAlivePacket(client.getNow()));
    }
}

