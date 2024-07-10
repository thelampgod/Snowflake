package com.github.thelampgod.snowflake.packets.impl;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class DisconnectPacket extends SnowflakePacket {
    private final String reason;

    public DisconnectPacket(String reason, ClientHandler sender) {
        super(sender);
        this.reason = reason;
    }

    public DisconnectPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(7);
        out.writeUTF(reason);
    }

    @Override
    public void handle() throws IOException {
        this.getSender().sendPacket(this);
        Snowflake.INSTANCE.getServer().removeClient(this.getSender());
    }

    public String getReason() {
        return reason;
    }
}
