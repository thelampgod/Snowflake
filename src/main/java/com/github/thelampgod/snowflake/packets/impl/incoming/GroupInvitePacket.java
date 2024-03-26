package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupConnectionPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupPasswordPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.GroupInfoPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupInvitePacket extends SnowflakePacket {

    private final int clientId;
    private final int groupId;
    private byte[] decryptKey;
    public GroupInvitePacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.clientId = in.readInt();
        this.groupId = in.readInt();
        this.decryptKey = new byte[in.readInt()];
        in.readFully(this.decryptKey);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        super.handle();

        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);
        if (group.getOwnerId() != this.getSender().getId()) {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("You are not the owner of this group."));
            return;
        }

        final ClientHandler invitedUser = Snowflake.INSTANCE.getServer().getClientReceiver(clientId).getConnection();

        group.addUser(clientId);
        invitedUser.sendPacket(new GroupInfoPacket(group.getName(), group.getId(), false, group.getUsers()));
        invitedUser.sendPacket(new GroupPasswordPacket(group.getId(), this.decryptKey));

        for (int user : group.getUsers()) {
            final ClientHandler client = Snowflake.INSTANCE.getServer().getClientReceiver(user).getConnection();
            client.sendPacket(new GroupConnectionPacket.Added(group.getId(), clientId));
        }
    }
}
