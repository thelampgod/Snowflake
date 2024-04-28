package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.UsersPacket;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;

public class ListUsersPacket extends SnowflakePacket {

    public ListUsersPacket(SocketClient sender) {
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
        for (SocketClient client : getConnectedClients()) {
            if (client.getId() == 0) continue;
            idToNameMap.put(client.getId(), client.getName());
        }

        this.getSender().getConnection().sendPacket(new UsersPacket(idToNameMap));
    }
}
