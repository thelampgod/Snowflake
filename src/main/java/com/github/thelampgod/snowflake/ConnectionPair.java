package com.github.thelampgod.snowflake;

import java.io.IOException;
import java.util.Objects;

import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class ConnectionPair {

    private ClientHandler talker;
    private ClientHandler receiver;

    private final String secret;

    public ConnectionPair(String secret) {
        this.secret = secret;
    }

    public ClientHandler getTalker() {
        return talker;
    }

    public ClientHandler getReceiver() {
        return receiver;
    }

    public boolean isPaired() {
        return receiver != null && talker != null;
    }

    public void setTalker(ClientHandler talker) {
        this.talker = talker;
    }

    public void setReceiver(ClientHandler receiver) {
        this.receiver = receiver;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionPair that = (ConnectionPair) o;
        return Objects.equals(talker, that.talker) && Objects.equals(receiver, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(talker, receiver);
    }

    public void close() {
        receiver.isRunning = false;
        talker.isRunning = false;
        try {
            receiver.client.getSocket().close();
            talker.client.getSocket().close();
        } catch (IOException e) {
            getLog().error("Error closing socket: " + e.getMessage(), e);
        }
    }

}
