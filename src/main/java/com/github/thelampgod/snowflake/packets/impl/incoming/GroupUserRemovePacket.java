package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupConnectionPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupUserRemovePacket extends SnowflakePacket {
    private final int groupId;
    private final int clientId;
    public GroupUserRemovePacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.groupId = in.readInt();
        this.clientId = in.readInt();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated() || !super.isOwner(groupId)) {
            return;
        }
        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);
        group.removeUser(clientId);

        for (int clientId : group.getUsers()) {
            final SocketClient user = Snowflake.INSTANCE.getServer().getClientReceiver(clientId);

            user.getConnection().sendPacket(new GroupConnectionPacket.Removed(groupId, this.clientId));
        }
    }
}
