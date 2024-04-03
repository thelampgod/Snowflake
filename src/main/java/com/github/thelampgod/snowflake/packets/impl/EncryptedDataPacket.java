package com.github.thelampgod.snowflake.packets.impl;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EncryptedDataPacket extends SnowflakePacket {

    private final boolean group;
    private final int id;
    private final byte[] data;
    public EncryptedDataPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.group = in.readBoolean();
        this.id = in.readInt();
        this.data = new byte[in.readInt()];
        in.readFully(data);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(21);
        out.writeBoolean(group);
        out.writeInt(this.getSender().getId());
        out.writeInt(id);
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated()) {
            return;
        }

        if (group) {
            forwardToGroup(id);
            return;
        }
        forwardToUser(id);
    }

    private void forwardToUser(int id) throws IOException {
        SocketClient client = Snowflake.INSTANCE.getServer().getClientReceiver(id);
        client.getConnection().sendPacket(this);
    }

    private void forwardToGroup(int id) throws IOException {
        final Group group = Snowflake.INSTANCE.getGroupManager().get(id);

        for (int user : group.getUsers()) {
            SocketClient client = Snowflake.INSTANCE.getServer().getClientReceiver(user);
            client.getConnection().sendPacket(this);
        }
    }
}
