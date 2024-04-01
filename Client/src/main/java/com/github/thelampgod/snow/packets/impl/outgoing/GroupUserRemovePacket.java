package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class GroupUserRemovePacket extends SnowflakePacket {
    private final int groupId;
    private final int clientId;

    public GroupUserRemovePacket(int groupId, int clientId) {
        this.groupId = groupId;
        this.clientId = clientId;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(16);
        out.writeInt(groupId);
        out.writeInt(clientId);
    }

    @Override
    public void handle() {

    }
}
