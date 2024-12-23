package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupConnectionPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupInfoPacket;
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
        int clientId = super.getSender().client.getId();

        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);
        if (!group.containsUser(clientId)) return;

        boolean ownerPicked = false;
        for (int client : group.getUsers()) {
            final ClientHandler user = Snowflake.INSTANCE.getServer().getClientReceiver(client);
            if (user == null) continue;

            if (!ownerPicked && group.getOwnerId() == clientId) {
                // Pick new group owner
                if (client != clientId) {
                    user.sendPacket(new GroupInfoPacket(group.getName(), groupId, true, group.getUsers()));
                    group.setOwner(client);
                    DatabaseUtil.updateOwner(client, groupId, Snowflake.INSTANCE.getDb());
                    ownerPicked = true;
                }
            }
            // Notify group users of user group leave
            user.sendPacket(new GroupConnectionPacket.Removed(groupId, clientId));
        }
        group.removeUser(clientId);
        if (group.getUsers().isEmpty()) {
            Snowflake.INSTANCE.getGroupManager().remove(group);
            DatabaseUtil.removeGroup(group, Snowflake.INSTANCE.getDb());
            return;
        }
        DatabaseUtil.removeUserFromGroup(clientId, group, Snowflake.INSTANCE.getDb());
    }
}
