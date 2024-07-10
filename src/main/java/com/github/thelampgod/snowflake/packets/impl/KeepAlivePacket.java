package com.github.thelampgod.snowflake.packets.impl;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class KeepAlivePacket extends SnowflakePacket {
    private final long timestamp;

    public KeepAlivePacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.timestamp = in.readLong();
    }

    public KeepAlivePacket(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(4);
        out.writeLong(timestamp);
    }

    @Override
    public void handle() throws IOException {
        SocketClient receiver = Snowflake.INSTANCE.getServer().getClientReceiver(this.getSender()).client;
        if (receiver == null) {
            this.getSender().sendPacket(new DisconnectPacket("No receiver?"));
            return;
        }
        long now = receiver.getNow();

        if (this.timestamp != now) {
            this.getSender().sendPacket(
                    new DisconnectPacket(String.format("Failed keepalive, expected %d, got %d", now, timestamp)));
            return;
        }
        receiver.responded = true;
        getLog().debug(receiver + " keepalive response in " + (System.currentTimeMillis() - now) + "ms");
    }
}
