package com.github.thelampgod.snow;

import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class ServerManager {
    private final LinkedBlockingQueue<WrappedComm> talkComms = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<WrappedComm> receiveComms = new LinkedBlockingQueue<>();

    private final HashSet<ServerHandler> threads = new HashSet<>();

    private static String sharedSecret = null;

    private final Map<Integer, String> connectedUsers = Maps.newConcurrentMap();

    private boolean isRunning = true;

    public ServerManager(String ip, int port) {
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

        } catch (IOException e) {
            Snow.instance.getLog().error(e.getMessage());
        }
    }

    private void listenForReceive() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    this.receiveComm((out, in) -> {
                        final SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in);
                        packet.handle();
                    });
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void close() throws IOException {
        for (ServerHandler thread : threads) {
            thread.socket.close();
            thread.isRunning = false;
        }
        isRunning = false;
        threads.clear();
    }


    public boolean isConnected() {
        return threads.stream().anyMatch(ServerHandler::isRunning);
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
        public boolean isRunning = true;
        private final boolean receiver;

        public ServerHandler(Socket socket, boolean receiver, LinkedBlockingQueue<WrappedComm> comms) {
            this.socket = socket;
            this.receiver = receiver;
            this.comms = comms;
        }

        @Override
        public void run() {
            try {
                printModMessage("Connected.");
                Snow.instance.getLog().info("Connected to snowflake");
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
                isRunning = false;
                printModMessage("Disconnected from snowflake");
            }
        }

        public boolean isRunning() {
            return this.isRunning;
        }
    }
}
