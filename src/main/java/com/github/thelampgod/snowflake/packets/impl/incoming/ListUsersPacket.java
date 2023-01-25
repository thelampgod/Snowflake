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
        JsonObject node = new JsonObject();
        final Map<Integer, String> idToNameMap = new HashMap<>();
        for (SocketClient client : getConnectedClients()) {
            idToNameMap.put(client.getId(), client.getName());

            JsonObject jsonClient = new JsonObject();
            jsonClient.addProperty(String.valueOf(client.getId()), client.getName());
            jsonClient.entrySet().forEach(entry -> node.add(entry.getKey(), entry.getValue()));
        }

        String list = new GsonBuilder().setPrettyPrinting().create().toJson(node);
        this.getSender().getConnection().sendPacket(new UsersPacket(idToNameMap));
        this.getSender().getConnection().sendPacket(new PlainMessagePacket(list));
    }
}
