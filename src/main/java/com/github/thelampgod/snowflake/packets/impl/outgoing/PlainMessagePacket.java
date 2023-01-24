package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class PlainMessagePacket extends SnowflakePacket {
    private final String message;
    public PlainMessagePacket(String message) throws IOException {
        super(SocketClient.Snowflake());
        this.message = message;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(message);
    }

    @Override
    public void handle() throws IOException {

    }
}
