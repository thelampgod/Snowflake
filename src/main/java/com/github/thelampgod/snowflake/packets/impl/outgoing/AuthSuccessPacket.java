package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class AuthSuccessPacket extends SnowflakePacket {

    public AuthSuccessPacket() throws IOException {
        super(SocketClient.Snowflake());
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeUTF("Successfully authenticated");
    }

    @Override
    public void handle() throws IOException {

    }
}
