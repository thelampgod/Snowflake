package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.util.Helper.printModMessage;

public class GroupRemovePacket extends SnowflakePacket {

    private final int groupId;

    public GroupRemovePacket(int groupId) {
        this.groupId = groupId;
    }

    public GroupRemovePacket(DataInputStream in) throws IOException {
        this.groupId = in.readInt();
    }
    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(15);
        out.writeInt(groupId);
    }

    @Override
    public void handle() {
        final Group group = Snow.instance.getGroupManager().get(groupId);
        Snow.instance.getGroupManager().remove(group);
        printModMessage(group.getName() + " was removed!");
    }
}
