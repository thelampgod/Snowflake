package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.google.common.collect.Sets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class GroupInfoPacket extends SnowflakePacket {

    private final String name;
    private final int id;
    private final boolean isOwner;
    private final Set<Integer> users = Sets.newHashSet();
    public GroupInfoPacket(DataInputStream in) throws IOException {
        this.name = in.readUTF();
        this.id = in.readInt();
        this.isOwner = in.readBoolean();
        for (int i = 0; i < in.readByte(); ++i) {
            users.add(in.readInt());
        }
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {
        Snow.instance.getGroupManager().add(new Group(name, id, isOwner, users));
    }
}
