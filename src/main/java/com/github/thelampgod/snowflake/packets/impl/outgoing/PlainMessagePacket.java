package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class PlainMessagePacket extends SnowflakePacket {
    private final String message;
    public PlainMessagePacket(String message) {
        this.message = message;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(9);
        out.writeUTF(message);
    }
}
