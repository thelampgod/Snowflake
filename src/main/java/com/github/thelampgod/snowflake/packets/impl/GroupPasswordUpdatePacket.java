package com.github.thelampgod.snowflake.packets.impl;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupPasswordUpdatePacket extends SnowflakePacket {

    private final int groupId;
    private final byte[] password;
    public GroupPasswordUpdatePacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.groupId = in.readInt();
        this.password = new byte[in.readInt()];
        in.readFully(password);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(19);
        out.writeInt(groupId);
        out.writeInt(password.length);
        out.write(password);
    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated() || !super.isOwner(groupId)) {
            return;
        }
        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);

        for (int user : group.getUsers()) {
            final ClientHandler client = Snowflake.INSTANCE.getServer().getClientReceiver(user);
            if (client == null) continue;
            client.sendPacket(this);
        }

    }
}
