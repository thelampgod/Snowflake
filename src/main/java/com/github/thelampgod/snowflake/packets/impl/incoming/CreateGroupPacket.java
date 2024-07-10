package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupCreateSuccessPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupInfoPacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CreateGroupPacket extends SnowflakePacket {

    private final String name;
    public CreateGroupPacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.name = in.readUTF();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated()) {
            return;
        }

        final Group group = new Group(this.name, this.getSender().client.getId());
        int groupId = DatabaseUtil.insertGroup(group, Snowflake.INSTANCE.getDb());
        group.setId(groupId);
        Snowflake.INSTANCE.getGroupManager().add(group);
        DatabaseUtil.addUserToGroup(this.getSender().client.getId(), group, Snowflake.INSTANCE.getDb());

        this.getSender().sendPacket(new GroupInfoPacket(group.getName(), group.getId(), true, group.getUsers()));
        this.getSender().sendPacket(new GroupCreateSuccessPacket(group.getId()));
    }
}
