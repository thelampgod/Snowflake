package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AddRecipientPacket extends SnowflakePacket {
    private final byte method;
    private int id = 0;
    private String key = "";

    public AddRecipientPacket(DataInputStream in) throws IOException {
        if ((this.method = in.readByte()) == 0) {
            this.id = in.readInt();
        } else {
            this.key = in.readUTF();
        }
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {

    }
}
