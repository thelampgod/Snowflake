package com.github.thelampgod.snow;

import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.DisconnectPacket;
import com.github.thelampgod.snow.util.Helper;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerManager {
    private final LinkedBlockingQueue<Comm> talkComms = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Comm> receiveComms = new LinkedBlockingQueue<>();
    private final HashSet<ServerHandler> handlers = new HashSet<>();
    private static String sharedSecret;
    private volatile boolean isRunning = false;
    private volatile long lastKeepAlive = 0L;

    private final String ip;
    private final int port;

    private final String password;

    private static final String PROTOCOL_VERSION = "uRedLELysIsMAndEt";

    public ServerManager(String ip, int port, String password) {
        this.ip = ip;
        this.port = port;
        this.password = password;
    }

    public ServerManager(String address) {
        String[] split = address.split(":");
        this.ip = split[0];
        this.port = Integer.parseInt(split[1]);
        this.password = "";
    }

    public synchronized void connect() {
        try {
            close();
            this.isRunning = false;
            sharedSecret = RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());
            ServerHandler receiver = new ServerHandler(createSocket(ip, port), true, receiveComms);
            ServerHandler talker = new ServerHandler(createSocket(ip, port), false, talkComms, password);
            handlers.add(receiver);
            handlers.add(talker);
            receiver.start();
            talker.start();
            listenForReceive();
            listenForReceive();
            listenForReceive();
            listenForReceive();
            this.isRunning = true;
            Snow.instance.load(this.ip + ":" + this.port);
        } catch (IOException e) {
            this.isRunning = false;
            Snow.instance.getLog().error("Error connecting to server: " + e.getMessage(), e);
        }
    }

    private void listenForReceive() {
        new Thread(() -> {
            try {
                while (isRunning) {
                    receiveComm((out, in) -> {
                        SnowflakePacket packet = SnowflakePacket.fromId(in.readByte(), in);
//                        Snow.instance.getLog().info("[IN] " + packet.getClass().getSimpleName());
                        try {
                            packet.handle();
                        } catch (Exception e) {
                            Snow.instance.getLog().error("Error in handling packet: " + e.getMessage(), e);
//                            this.close();
                        }
                    });
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Snow.instance.getLog().error("Receive thread interrupted: " + e.getMessage(), e);
            }
        }).start();
    }

    public boolean isConnected() {
        return isRunning;
    }

    public synchronized void close() {
        if (!isRunning) return;
        this.sendPacket(new DisconnectPacket());
        for (ServerHandler handler : handlers) {
            try {
                handler.socket.close();
            } catch (IOException e) {
                Snow.instance.getLog().error("Error closing socket: " + e.getMessage(), e);
            }
            handler.isRunning = false;
        }
        handlers.clear();
        isRunning = false;
        Snow.instance.save(this.ip + ":" + this.port);
    }

    public void receiveComm(Comm comm) throws InterruptedException {
        receiveComms.add(comm);
        if (receiveComms.size() > 500) {
            for (int i = 0; i < 250; ++i) {
                receiveComms.take();
            }
        }
    }

    public void sendComm(Comm comm) {
        talkComms.add(comm);
    }

    private static Socket createSocket(String ip, int port) throws IOException {
        Snow.instance.getLog().info("Connecting to snowflake");
        return new Socket(ip, port);
    }

    public void sendPacket(SnowflakePacket packet) {
//        Snow.instance.getLog().info("[OUT] " + packet.getClass().getSimpleName());
        sendComm((out, in) -> {
            packet.writeData(out);
            out.flush();
        });
    }

    @FunctionalInterface
    public interface Comm {
        void run(DataOutputStream out, DataInputStream in) throws Exception;
    }

    private static class ServerHandler extends Thread {
        private final Socket socket;
        private final LinkedBlockingQueue<Comm> comms;
        private final String password;
        private boolean isRunning = false;
        private final boolean receiver;

        ServerHandler(Socket socket, boolean receiver, LinkedBlockingQueue<Comm> comms, String password) {
            this.socket = socket;
            this.receiver = receiver;
            this.comms = comms;
            this.password = password;
        }

        ServerHandler(Socket socket, boolean receiver, LinkedBlockingQueue<Comm> comms) {
            this(socket, receiver, comms, "");
        }

        @Override
        public void run() {
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                 DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

                socket.setSoTimeout(15000);
                if (!receiver) {
                    Helper.addToast("Connected");
                }
                Snow.instance.getLog().info("Connected to snowflake");
                this.isRunning = true;

                out.writeBoolean(receiver);
                out.writeUTF(sharedSecret);
                out.flush();

                if (!receiver) {
                    out.writeUTF(PROTOCOL_VERSION);
                    out.writeUTF(password);
                    out.flush();
                }

                while (isRunning) {
                    Comm comm = comms.take();
                    comm.run(out, in);
                }
            } catch (Exception e) {
                this.isRunning = false;
                Snow.instance.getLog().info((receiver ? "Receiver" : "Sender") + " handler disconnected");
                if (!receiver) {
                    Helper.addToast("Disconnected");
                }
            }
        }
    }
}
