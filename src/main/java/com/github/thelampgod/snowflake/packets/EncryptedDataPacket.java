package com.github.thelampgod.snowflake.packets;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EncryptedDataPacket extends SnowflakePacket {

    private final int groupId;
    private final byte[] data;
    public EncryptedDataPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.groupId = in.readInt();
        this.data = new byte[in.readInt()];
        in.readFully(data);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(21);
        out.writeInt(groupId);
        out.writeInt(data.length);
        out.write(data);
    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated()) {
            return;
        }

        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);

        for (int user : group.getUsers()) {
            SocketClient client = Snowflake.INSTANCE.getServer().getClientReceiver(user);
            client.getConnection().sendPacket(this);
        }
    }
}
