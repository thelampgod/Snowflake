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
    public boolean isRunning = true;
    private DataOutputStream out;
    private DataInputStream in;

    public final HashSet<Integer> recipientsIds = new HashSet<>();
    private final Queue<SnowflakePacket> outboundPacketsQueue = new ConcurrentLinkedQueue<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public ClientHandler(Socket socket) throws IOException {
        this.client = new SocketClient(socket, this);
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
                runSendTick();
                client.setReceiver(true);
                getLog().debug("Receiver connected " + client);
                // keep the thread alive, so it is ready to send packets
                while (isRunning) {
                    Thread.sleep(15000);
                    sendKeepAlive();
                }
                return;
            }

            getLog().info(client + " connected.");
            getLog().debug("Talker connected " + client);
            while (isRunning) {
                SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in, client);
                packet.handle();
                getLog().debug("Received a " + packet.getClass().getSimpleName() + " from " + client);
            }
        } catch (Throwable th) {
            th.printStackTrace();
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
    }

    public void sendPacket(SnowflakePacket packet) throws IOException {
        if (!this.client.isReceiver()) {
            for (SocketClient receiver : getConnectedClients()) {
                if (!receiver.isReceiver()) continue;
                if (receiver.getLinkString().equals(this.client.getLinkString())) {
                    receiver.getConnection().sendPacket(packet);
                    return;
                }
            }
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
        getLog().debug("sending keepalive to " + client);
        client.setNow(System.currentTimeMillis());
        this.dispatchPacket(new KeepAlivePacket(client.getNow()));
    }
}

