package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.util.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupCreateSuccessPacket extends SnowflakePacket {
    private final int groupId;

    public GroupCreateSuccessPacket(DataInputStream in) throws IOException {
        this.groupId = in.readInt();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {
        final Group group = Snow.instance.getGroupManager().get(groupId);
        group.setPassword(EncryptionUtil.generatePassword());
    }
}
