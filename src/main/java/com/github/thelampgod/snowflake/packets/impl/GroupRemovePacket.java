package com.github.thelampgod.snowflake.packets.impl;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupRemovePacket extends SnowflakePacket {
    private final int groupId;
    public GroupRemovePacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.groupId = in.readInt();
    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated() || !super.isOwner(groupId)) {
            return;
        }
        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);

        for (int clientId : group.getUsers()) {
            ClientHandler client = Snowflake.INSTANCE.getServer().getClientReceiver(clientId);
            if (client == null) continue;
            client.sendPacket(this);
        }

        DatabaseUtil.removeGroup(group, Snowflake.INSTANCE.getDb());
        Snowflake.INSTANCE.getGroupManager().remove(group);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(20);
        out.writeInt(groupId);
    }
}
