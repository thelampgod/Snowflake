package com.github.thelampgod.snowflake;

import java.net.Socket;

public class SocketClient {
    private final Socket socket;

    private String name = null;
    private String pubKey = null;
    private int id;

    public SocketClient(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public String getPubKey() {
        return pubKey;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        if (isAuthenticated()) {
            return String.format("%s (%s:%s)", this.name, this.socket.getInetAddress().getHostName(), this.socket.getPort());
        }

        return this.socket.getInetAddress().getHostName() + ":" + this.socket.getPort();
    }

    public boolean isAuthenticated() {
        return this.name != null && this.pubKey != null;
    }
}
