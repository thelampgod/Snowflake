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
    //TODO: move id and stuff out of SocketClient
    public final SocketClient client;
    private final String password;
    private static final String PROTOCOL_VERSION = "uRedLELysIsMAndEt";
    public boolean isRunning = true;
    private DataOutputStream out;
    private DataInputStream in;
    private final Queue<SnowflakePacket> outboundPacketsQueue = new ConcurrentLinkedQueue<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ClientHandler(Socket socket, String password) throws IOException {
        this.client = new SocketClient(socket, this);
        this.password = password;
    }

    @Override
    public void run() {
        try {
            out = client.getOutputStream();
            in = client.getInputStream();

            client.getSocket().setSoTimeout(15000);
            boolean receiver = in.readBoolean();
            String secret = in.readUTF();
            client.setLinker(secret);

            getServer().addClient(receiver, this);

            if (!receiver) {
                if (!in.readUTF().equals(PROTOCOL_VERSION)) {
                    this.sendPacket(new DisconnectPacket("Outdated client or server"));
                    return;
                }
                if (!in.readUTF().equals(password) && !password.isEmpty()) {
                    this.sendPacket(new DisconnectPacket("Wrong password"));
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
                SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in, this);
                getLog().debug("Received a " + packet.getClass().getSimpleName() + " from " + client);
                packet.handle();
            }
        } catch (Throwable ignored) {
            try {
                getServer().removeClient(client);
//                getLog().error("Error in packet handle: " + ignored.getMessage(), ignored);
            } catch (IOException e) {

            }
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
        if (packet instanceof MultiPacketPacket multi) {
            for (SnowflakePacket p : multi.getPackets()) {
                getLog().debug("    Sending a " + p.getClass().getSimpleName() + " to " + client);
            }
        } else {
            getLog().debug("Sending a " + packet.getClass().getSimpleName() + " to " + client);
        }

        packet.writeData(out);
        out.flush();

        if (packet instanceof DisconnectPacket disconnect) {
            if (disconnect.getSender() == null) return;
            getServer().removeClient(disconnect.getSender());
            getLog().debug(disconnect.getSender().client + " disconnected. Reason: " + disconnect.getReason());
        }
    }

    public void sendPacketInstant(SnowflakePacket packet) {
        try {
            this.dispatchPacket(packet);
        } catch (IOException e) {
            getLog().error("Error sending packet: " + e.getMessage(), e);
        }
    }

    public void sendPacket(SnowflakePacket packet) {
        if (!this.client.isReceiver()) {
            ClientHandler receiver = Snowflake.INSTANCE.getServer().getClientReceiver(this.client);
            if (receiver == null) return;
            receiver.sendPacket(packet);
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
            this.sendPacket(new DisconnectPacket("Timed out."));
            return;
        }
        client.responded = false;
        client.setNow(System.currentTimeMillis());
        this.dispatchPacket(new KeepAlivePacket(client.getNow()));
    }
}

