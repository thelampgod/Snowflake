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

public class GroupLeavePacket extends SnowflakePacket {

    private final int groupId;
    public GroupLeavePacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.groupId = in.readInt();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated()) {
            return;
        }
        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);
        if (!group.containsUser(super.getSender().client.getId())) return;

        for (int clientId : group.getUsers()) {
            final ClientHandler user = Snowflake.INSTANCE.getServer().getClientReceiver(clientId);
            if (user == null) continue;

            user.sendPacket(new GroupConnectionPacket.Removed(groupId, super.getSender().client.getId()));
        }
        group.removeUser(super.getSender().client.getId());
        if (group.getUsers().isEmpty()) {
            Snowflake.INSTANCE.getGroupManager().remove(group);
            DatabaseUtil.removeGroup(group, Snowflake.INSTANCE.getDb());
            return;
        }
        DatabaseUtil.removeUserFromGroup(super.getSender().client.getId(), group, Snowflake.INSTANCE.getDb());

    }
}
