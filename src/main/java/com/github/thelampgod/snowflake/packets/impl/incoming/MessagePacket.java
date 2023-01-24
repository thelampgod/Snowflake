package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessagePacket extends SnowflakePacket {
    private final byte[] message;
    private final boolean encrypted;

    public MessagePacket(DataInputStream in, boolean encrypted) throws IOException {
        this.message = new byte[in.readInt()];
        in.readFully(this.message);
        this.encrypted = encrypted;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {

    }

    public static class Plain extends MessagePacket {
        public Plain(DataInputStream in) throws IOException {
            super(in, false);
        }
    }

    public static class Encrypted extends MessagePacket {
        public Encrypted(DataInputStream in) throws IOException {
            super(in, true);
        }
    }
}
