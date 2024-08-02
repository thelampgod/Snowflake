package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.ConnectionPair;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.*;
import com.github.thelampgod.snowflake.packets.impl.DisconnectPacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class HandshakeResponsePacket extends SnowflakePacket {
    private final String secret;
    private final String name;

    public HandshakeResponsePacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.secret = in.readUTF();
        this.name = in.readUTF();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        final SocketClient client = this.getSender().client;
        if (this.secret.equals(client.getSecret())) {
            final String pubKey = client.getPubKey();
            final String name = this.name;

            client.setName(name);
            int id = DatabaseUtil.insertUser(client);

            // check that user is not already connected and authenticated
            if (Snowflake.INSTANCE.getServer().getClientReceiver(id) != null) {
                client.getConnection().sendPacket(new DisconnectPacket("You're already connected! Choose another identity."));
                return;
            }

            client.setId(id);
            client.setAuthenticated(true);

            SocketClient receiver = Snowflake.INSTANCE.getServer().getClientReceiver(client.getLinkString()).client;
            receiver.setPubKey(pubKey);
            receiver.setName(name);
            receiver.setId(id);
            receiver.setAuthenticated(true);
            getLog().info(client + " authenticated.");

            client.getConnection().sendPacket(new AuthSuccessPacket(client.getId()));
            announceConnection();

            final Set<Group> userGroups = Snowflake.INSTANCE.getGroupManager().findUserGroups(client.getId());
            for (Group group : userGroups) {
                client.getConnection().sendPacket(new GroupInfoPacket(group.getName(), group.getId(), group.getOwnerId() == client.getId(), group.getUsers()));
                for (int groupUserId : group.getUsers()) {
                    final ClientHandler groupUser = Snowflake.INSTANCE.getServer().getClientReceiver(groupUserId);
                    if (groupUser == null) continue;
                    groupUser.sendPacket(new GroupConnectionPacket.Joined(group.getId(), client.getId()));
                }
            }
        } else {
            client.getConnection().sendPacket(new DisconnectPacket("Wrong password"));
        }
    }

    private void announceConnection() {
        for (ConnectionPair pair : Snowflake.INSTANCE.getServer().getConnections()) {
            ClientHandler receiver = pair.getReceiver();
            if (!receiver.client.isAuthenticated()) continue;

            receiver.sendPacket(new ConnectionPacket.Connect(this.getSender().client.getId(), this.getSender().client.getName()));
        }
    }
}
