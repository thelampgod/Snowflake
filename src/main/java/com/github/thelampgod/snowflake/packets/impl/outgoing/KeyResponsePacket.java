package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class KeyResponsePacket extends SnowflakePacket {
    private final int id;
    private final String key;
    public KeyResponsePacket(int id, String key) throws IOException {
        super(SocketClient.Snowflake());
        this.id = id;
        this.key = key;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeInt(id);
        out.writeUTF(key);
    }

    @Override
    public void handle() throws IOException {

    }
}
