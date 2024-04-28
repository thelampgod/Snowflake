package com.github.thelampgod.snowflake;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private ClientHandler connection;
    private String secret;
    private DataOutputStream out;
    private DataInputStream in;
    public boolean responded = true;

    private String name = "not_authenticated_user";
    private String pubKey = null;
    private boolean authenticated = false;
    private int id;
    private boolean receiver = false;
    private String linkString;

    private long now;

    public SocketClient(Socket socket, ClientHandler connection) throws IOException {
        this.socket = socket;
        this.connection = connection;
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public SocketClient() {
    }

    public static SocketClient Snowflake() throws IOException {
        return new SocketClient();
    }

    public Socket getSocket() {
        return socket;
    }

    public DataOutputStream getOutputStream() {
        return out;
    }

    public DataInputStream getInputStream() {
        return in;
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

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean isAuthenticated() {
        return authenticated && this.name != null && this.pubKey != null;
    }

    public void setReceiver(boolean receiver) {
        this.receiver = receiver;
    }

    public boolean isReceiver() {
        return this.receiver;
    }

    public void setLinker(String secret) {
        this.linkString = secret;
    }

    public String getLinkString() {
        return linkString;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getNow() {
        return now;
    }

    public ClientHandler getConnection() {
        return this.connection;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return this.secret;
    }
}
