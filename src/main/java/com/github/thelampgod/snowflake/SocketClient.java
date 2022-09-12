package com.github.thelampgod.snowflake;

import java.net.Socket;

public class SocketClient {
    private final Socket socket;

    private String name = null;
    private String pubKey = null;

    public SocketClient(Socket socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        if (isAuthenticated()) {
            return String.format("%s (%s:%s)", this.name, this.socket.getInetAddress().getHostName(), this.socket.getPort());
        }

        return this.socket.getInetAddress().getHostName() + this.socket.getPort();
    }

    public boolean isAuthenticated() {
        return this.name != null && this.pubKey != null;
    }
}
