package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class DisconnectPacket extends SnowflakePacket {
    private final String reason;

    public DisconnectPacket(String reason) throws IOException {
        super(SocketClient.Snowflake());
        this.reason = reason;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(-99);
        out.writeUTF(reason);
    }

    @Override
    public void handle() throws IOException {

    }
}
