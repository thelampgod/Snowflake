package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupInfoPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CreateGroupPacket extends SnowflakePacket {

    private final String name;
    public CreateGroupPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.name = in.readUTF();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        super.handle();
        final Group group = new Group(this.name, this.getSender().getId());
        Snowflake.INSTANCE.getGroupManager().add(group);

        this.getSender().getConnection().sendPacket(new GroupInfoPacket(group.getName(), group.getId(), true, group.getUsers()));
    }
}
