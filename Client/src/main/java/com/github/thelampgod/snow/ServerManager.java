package com.github.thelampgod.snow;

import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.KeepAlivePacket;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ServerManager {
    private final LinkedBlockingQueue<WrappedComm> talkComms = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<WrappedComm> receiveComms = new LinkedBlockingQueue<>();

    private final HashSet<ServerHandler> threads = new HashSet<>();

    private static String sharedSecret = null;
    public final String address;

    private boolean isRunning = false;
    private long lastKeepAlive = 0L;

    public ServerManager(String ip, int port) {
        this.address = ip + ":" + port;
        sharedSecret = RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
        try {
            ServerHandler receiver = new ServerHandler(connect(ip, port), true, receiveComms);
            ServerHandler talker = new ServerHandler(connect(ip, port), false, talkComms);
            threads.add(receiver);
            threads.add(talker);
            receiver.start();
            talker.start();
            listenForReceive();
            listenForReceive();
            this.isRunning = true;
        } catch (IOException e) {
            this.isRunning = false;
            Snow.instance.getLog().error(e.getMessage());
        }
    }

    private void listenForReceive() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    this.receiveComm((out, in) -> {
                        final SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in);
                        if (packet instanceof KeepAlivePacket p) {
                            lastKeepAlive = p.getTimestamp();
                        }
                        packet.handle();
                    });
                    if (lastKeepAlive != 0 && System.currentTimeMillis() - lastKeepAlive > TimeUnit.SECONDS.toMillis(30)) {
                        this.close();
                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    public boolean isConnected() {
        return isRunning;
    }

    public void close() throws IOException {
        for (ServerHandler thread : threads) {
            thread.socket.close();
            thread.isRunning = false;
        }
        isRunning = false;
        threads.clear();
    }

    public void receiveComm(Comm comm) throws InterruptedException {
        receiveComms.add(new WrappedComm(comm, true));
        if (receiveComms.size() > 500) {
            for (int i = 0; i < 250; ++i) {
                receiveComms.take();
            }
        }
    }

    public void serializedComm(Comm comm) {
        talkComms.add(new WrappedComm(comm, true));
    }

    private static Socket connect(String ip, int port) throws IOException {
        Snow.instance.getLog().info("Connecting to snowflake");
        return new Socket(ip, port);
    }

    public void sendPacket(SnowflakePacket packet) {
        this.serializedComm((out, in) -> {
            packet.writeData(out);
            out.flush();
        });
    }


    private static class WrappedComm {
        Comm comm;
        CompletableFuture<Boolean> done;
        boolean serialized;

        public WrappedComm(Comm comm, boolean serialized) {
            this.comm = comm;
            this.serialized = serialized;
            this.done = new CompletableFuture<>();
        }
    }

    @FunctionalInterface
    public interface Comm {

        void run(DataOutputStream out, DataInputStream in) throws Exception;
    }

    private static class ServerHandler extends Thread {
        private final Socket socket;
        private final LinkedBlockingQueue<WrappedComm> comms;
        public boolean isRunning = false;
        private final boolean receiver;

        public ServerHandler(Socket socket, boolean receiver, LinkedBlockingQueue<WrappedComm> comms) {
            this.socket = socket;
            this.receiver = receiver;
            this.comms = comms;
        }

        @Override
        public void run() {
            try {
                if (!receiver) {
                    Helper.addToast("Connected");
                }
                Snow.instance.getLog().info("Connected to snowflake");
                this.isRunning = true;
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                out.writeBoolean(receiver);
                // link receiver and talker
                out.writeUTF(sharedSecret);
                out.flush();

                while (isRunning) {
                    WrappedComm comm = comms.take();
                    comm.comm.run(out, in);
                    comm.done.complete(true);
                }

            } catch (Throwable th) {
                th.printStackTrace();
                Snow.getServerManager().isRunning = false;
                isRunning = false;
                if (!receiver) {
                    Helper.addToast("Disconnected");
                }
            }
        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }
}
