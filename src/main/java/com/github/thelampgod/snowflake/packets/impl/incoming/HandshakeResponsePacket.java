package com.github.thelampgod.snowflake.packets.impl.incoming;

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

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;
import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class HandshakeResponsePacket extends SnowflakePacket {
    private final String secret;
    private final String name;

    public HandshakeResponsePacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.secret = in.readUTF();
        this.name = in.readUTF();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        final SocketClient client = this.getSender();
        if (this.secret.equals(client.getSecret())) {
            final String pubKey = client.getPubKey();
            final String name = this.name;

            client.setName(name);
            int id = DatabaseUtil.insertUser(client);

            // check that user is not already connected and authenticated
            if (Snowflake.INSTANCE.getServer().getClientReceiver(id) != null) {
                client.getConnection().sendPacket(new DisconnectPacket("You're already connected! Choose another identity.", client));
                return;
            }

            client.setId(id);
            client.setAuthenticated(true);

            getConnectedClients().stream()
                    .filter(c -> c.getLinkString().equals(client.getLinkString()))
                    .filter(SocketClient::isReceiver)
                    .forEach(c -> {
                        c.setPubKey(pubKey);
                        c.setName(name);
                        c.setId(id);
                        c.setAuthenticated(true);
                        getLog().debug(c + " authenticated.");
                    });
            getLog().info(client + " authenticated.");

            final Set<Group> userGroups = Snowflake.INSTANCE.getGroupManager().findUserGroups(client.getId());
            for (Group group : userGroups) {
                client.getConnection().sendPacket(new GroupInfoPacket(group.getName(), group.getId(), group.getOwnerId() == client.getId(), group.getUsers()));
                for (int groupUsersIds : group.getUsers()) {
                    final SocketClient groupUser = Snowflake.INSTANCE.getServer().getClientReceiver(groupUsersIds);
                    if (groupUser == null) continue;
                    groupUser.getConnection().sendPacket(new GroupConnectionPacket.Joined(group.getId(), client.getId()));
                }
            }

            client.getConnection().sendPacket(new PlainMessagePacket("Successfully authenticated"));
            client.getConnection().sendPacket(new AuthSuccessPacket(client.getId()));
            sendConnectionMsg();
        } else {
            client.getConnection().sendPacket(new DisconnectPacket("Wrong password", client));
        }
    }

    private void sendConnectionMsg() throws IOException {
        for (SocketClient receiver : getConnectedClients()) {
            if (!receiver.isReceiver()) continue;
            if (!receiver.isAuthenticated()) continue;

            receiver.getConnection().sendPacket(new ConnectionPacket.Connect(this.getSender().getId(), this.getSender().getName()));
        }
    }
}
