package com.github.thelampgod.snow.packets;

import com.github.thelampgod.snow.packets.impl.LocationPacket;
import com.github.thelampgod.snow.packets.impl.MessagePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class WrappedPacket extends SnowflakePacket {
    private int sender;

    protected final static String DIVIDER = "\u00a7";

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public static WrappedPacket fromBytes(byte[] bytes) {
        int id = Integer.parseInt(new String(bytes).substring(0,1));
        if (id == 0) {
            return MessagePacket.fromBytes(bytes);
        }
        return new LocationPacket(bytes);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        throw new UnsupportedOperationException(
                this.getClass().getSimpleName() + " cannot be sent on its own, should be wrapped in an EncryptedDataPacket");
    }

    @Override
    public void handle() {
    }

    public abstract byte[] data();
}
