package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LocationPacket extends SnowflakePacket {
    private final double posX;
    private final double posY;
    private final double posZ;
    private final byte dimensionId;

    public LocationPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.posX = in.readDouble();
        this.posY = in.readDouble();
        this.posZ = in.readDouble();
        this.dimensionId = in.readByte();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {
        final SocketClient sender = this.getSender();
        if (!sender.isAuthenticated()) return;


    }
}
