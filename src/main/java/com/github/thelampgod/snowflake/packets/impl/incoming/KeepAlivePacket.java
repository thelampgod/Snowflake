package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class KeepAlivePacket extends SnowflakePacket {
    private final long timestamp;

    public KeepAlivePacket(DataInputStream in) throws IOException {
        this.timestamp = in.readLong();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {

    }
}
