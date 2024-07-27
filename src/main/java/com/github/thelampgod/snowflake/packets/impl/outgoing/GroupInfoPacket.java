package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class GroupInfoPacket extends SnowflakePacket {

    private final String name;
    private final int id;
    private final boolean isOwner;
    private final List<Integer> users;
    public GroupInfoPacket(String name, int id, boolean isOwner, List<Integer> users) {
        this.name = name;
        this.id = id;
        this.isOwner = isOwner;
        this.users = users;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(16);
        out.writeUTF(name);
        out.writeInt(id);
        out.writeBoolean(isOwner);
        out.writeByte(users.size());
        for (int i : users) {
            out.writeInt(i);
        }
    }
}
