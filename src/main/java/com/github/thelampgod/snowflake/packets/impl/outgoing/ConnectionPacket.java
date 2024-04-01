package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class ConnectionPacket extends SnowflakePacket {
    private final boolean connect;
    private final int id;
    private final String name;

    public ConnectionPacket(boolean connect, int id, String name) throws IOException {
        super(SocketClient.Snowflake());
        this.connect = connect;
        this.id = id;
        this.name = name;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte((connect) ? 5 : 6);
        out.writeInt(id);
        out.writeUTF(name);
    }


    public static class Connect extends ConnectionPacket {
        public Connect(int id, String name) throws IOException {
            super(true, id, name);
        }
    }

    public static class Disconnect extends ConnectionPacket {
        public Disconnect(int id, String name) throws IOException {
            super(false, id, name);
        }
    }
}



