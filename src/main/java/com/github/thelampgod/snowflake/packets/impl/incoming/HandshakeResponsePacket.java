package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.AuthSuccessPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.ConnectionPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.DisconnectPacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
            client.setId(id);
            client.setAuthenticated(true);
            client.getConnection().recipientsIds.addAll(DatabaseUtil.getRecipientsFromDatabase(id));

            getConnectedClients().stream()
                    .filter(c -> c.getLinkString().equals(client.getLinkString()))
                    .filter(SocketClient::isReceiver)
                    .forEach(c -> {
                        c.setPubKey(pubKey.getBytes());
                        c.setName(name);
                        c.setId(id);
                        c.setAuthenticated(true);
                        getLog().debug(c + " authenticated.");
                    });

            getLog().info(client + " authenticated.");
            sendConnectionMsg();


            client.getConnection().sendPacket(new PlainMessagePacket("Successfully authenticated"));
            client.getConnection().sendPacket(new AuthSuccessPacket());
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
