package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.ConnectionPair;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.UsersPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListUsersPacket extends SnowflakePacket {

    public ListUsersPacket(ClientHandler sender) {
        super(sender);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated()) {
            return;
        }

        final Map<Integer, String> idToNameMap = new HashMap<>();
        for (ConnectionPair pair : Snowflake.INSTANCE.getServer().getConnections()) {
            SocketClient client = pair.getReceiver().client;
            if (client.getId() == -1) continue;
            idToNameMap.put(client.getId(), client.getName());
        }

        this.getSender().sendPacket(new UsersPacket(idToNameMap));
    }
}
