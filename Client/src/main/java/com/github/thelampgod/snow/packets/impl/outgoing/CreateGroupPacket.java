package com.github.thelampgod.snow.packets.impl.outgoing;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class CreateGroupPacket extends SnowflakePacket {

    private final String name;

    public CreateGroupPacket(String name) {
        this.name = name;
    }


    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(12);
        out.writeUTF(name);
    }

    @Override
    public void handle() {

    }
}
