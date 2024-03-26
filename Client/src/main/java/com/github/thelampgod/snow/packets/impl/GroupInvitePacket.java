package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class GroupInvitePacket extends SnowflakePacket {

    private final int clientId;
    private final int groupId;
    private byte[] decryptKey;

    public GroupInvitePacket(int clientId, int groupId, byte[] decryptKey) {
        this.clientId = clientId;
        this.groupId = groupId;
        this.decryptKey = decryptKey;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(13);
        out.writeInt(clientId);
        out.writeInt(groupId);

        out.writeInt(decryptKey.length);
        out.write(decryptKey);
    }

    @Override
    public void handle() {

    }
}
