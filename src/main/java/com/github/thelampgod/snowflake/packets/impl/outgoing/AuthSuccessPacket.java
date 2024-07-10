package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class AuthSuccessPacket extends SnowflakePacket {

    private final int authedUserId;

    public AuthSuccessPacket(int id) {
        this.authedUserId = id;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(13);
        out.writeInt(authedUserId);
    }
}
