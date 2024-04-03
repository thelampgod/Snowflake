package com.github.thelampgod.snow.packets;

import java.io.DataOutputStream;
import java.io.IOException;

public class WrappedPacket extends SnowflakePacket {
    private int sender;

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {

    }
}
