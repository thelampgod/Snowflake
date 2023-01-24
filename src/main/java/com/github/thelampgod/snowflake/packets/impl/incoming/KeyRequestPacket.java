package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeyRequestPacket extends SnowflakePacket {
    private final byte id;
    public KeyRequestPacket(DataInputStream in) throws IOException {
        this.id = in.readByte();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {

    }
}
