package com.github.thelampgod.snowflake.packets.impl;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class DisconnectPacket extends SnowflakePacket {
    private final String reason;

    public DisconnectPacket(String reason, SocketClient sender) {
        super(sender);
        this.reason = reason;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(7);
        out.writeUTF(reason);
    }

    @Override
    public void handle() throws IOException {
        this.getSender().getConnection().sendPacket(this);
        Snowflake.INSTANCE.getServer().removeClient(this.getSender());
    }

    public String getReason() {
        return reason;
    }
}
