package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class GroupCreateSuccessPacket extends SnowflakePacket {
    private final int groupId;
    public GroupCreateSuccessPacket(int id) throws IOException {
        super(SocketClient.Snowflake());
        this.groupId = id;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(22);
        out.writeInt(groupId);
    }
}
