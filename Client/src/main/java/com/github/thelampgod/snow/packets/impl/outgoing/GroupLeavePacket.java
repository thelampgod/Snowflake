package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class GroupLeavePacket extends SnowflakePacket {
    private final int groupId;

    public GroupLeavePacket(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(18);
        out.writeInt(groupId);
    }

    @Override
    public void handle() {
    }
}
