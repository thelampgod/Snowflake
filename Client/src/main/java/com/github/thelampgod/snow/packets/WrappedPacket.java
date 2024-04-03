package com.github.thelampgod.snow.packets;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class WrappedPacket extends SnowflakePacket {
    private int sender;

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        throw new UnsupportedOperationException(
                this.getClass().getSimpleName() + " cannot be sent on its own, should be wrapped in an EncryptedDataPacket");
    }

    @Override
    public void handle() {

    }
}
