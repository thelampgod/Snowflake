package com.github.thelampgod.snow.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupConnectionPacket extends SnowflakePacket {

    private final int action;
    private final int groupId;
    private final int clientId;
    public GroupConnectionPacket(DataInputStream in) throws IOException {
        this.action = in.readByte();
        this.groupId = in.readInt();
        this.clientId = in.readInt();
    }


    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {
        //todo
    }
}
