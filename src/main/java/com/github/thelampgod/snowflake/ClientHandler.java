package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.incoming.KeepAlivePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.DisconnectPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.github.thelampgod.snowflake.util.Helper.*;
import static net.daporkchop.lib.logging.Logging.logger;

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
                logger.debug("Receiver connected " + client);
                // keep the thread alive, so it is ready to send packets
                while (isRunning) {
                    Thread.sleep(15000);
                    sendKeepAlive();
                }
                return;
            }

            logger.info(client + " connected.");
            logger.debug("Talker connected " + client);
            while (isRunning) {
                try {
                    SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in, client);
                    packet.handle();
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (Throwable th) {
            getServer().removeClient(client);
        }
    }

    private void runSendTick() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    flushOutboundQueue();

                    Thread.sleep(50); // 20 tps
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void flushOutboundQueue() throws IOException {
        readWriteLock.readLock().lock();

        try {
            while (!outboundPacketsQueue.isEmpty()) {
                this.dispatchPacket(outboundPacketsQueue.poll());
            }

        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    private void dispatchPacket(SnowflakePacket packet) throws IOException {
        packet.writeData(out);
        out.flush();
    }

    public void sendPacket(SnowflakePacket packet) {
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
        logger.debug("sending keepalive to " + client);
        client.setNow(System.currentTimeMillis());

        this.sendPacket(new KeepAlivePacket(client.getNow()));
    }
}

