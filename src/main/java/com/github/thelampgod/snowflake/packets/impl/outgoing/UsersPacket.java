package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class UsersPacket extends SnowflakePacket {
    private final Map<Integer, String> idToNameMap;
    public UsersPacket(Map<Integer, String> idToNameMap) throws IOException {
        super(SocketClient.Snowflake());
        this.idToNameMap = idToNameMap;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(12);
        out.writeByte(idToNameMap.size());
        for (Map.Entry<Integer, String> entry : idToNameMap.entrySet()) {
            out.writeInt(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }
}
