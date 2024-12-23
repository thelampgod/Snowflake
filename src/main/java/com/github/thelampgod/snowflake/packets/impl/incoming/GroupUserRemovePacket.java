package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupConnectionPacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupUserRemovePacket extends SnowflakePacket {
    private final int groupId;
    private final int clientId;
    public GroupUserRemovePacket(DataInputStream in, ClientHandler sender) throws IOException {
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

        for (int clientId : group.getUsers()) {
            final ClientHandler client = Snowflake.INSTANCE.getServer().getClientReceiver(clientId);
            if (client == null) continue;
            client.sendPacket(new GroupConnectionPacket.Removed(groupId, this.clientId));
        }
        group.removeUser(clientId);
        DatabaseUtil.removeUserFromGroup(clientId, group, Snowflake.INSTANCE.getDb());
    }
}
