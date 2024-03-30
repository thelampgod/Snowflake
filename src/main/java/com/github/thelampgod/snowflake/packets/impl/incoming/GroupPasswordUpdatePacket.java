package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupConnectionPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupPasswordPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupPasswordUpdatePacket extends SnowflakePacket {

    private final int groupId;
    private final byte[] password;
    public GroupPasswordUpdatePacket(DataInputStream in, SocketClient sender) throws IOException {
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
        super.handle();

        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);

        if (group.getOwnerId() != this.getSender().getId()) {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("You are not the owner of this group."));
            return;
        }


        for (int user : group.getUsers()) {
            final ClientHandler client = Snowflake.INSTANCE.getServer().getClientReceiver(user).getConnection();
            client.sendPacket(this);
        }

    }
}
