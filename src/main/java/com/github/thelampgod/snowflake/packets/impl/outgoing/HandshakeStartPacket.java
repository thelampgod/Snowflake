package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class HandshakeStartPacket extends SnowflakePacket {
    private final byte[] encryptedSecret;
    public HandshakeStartPacket(byte[] encryptedSecret) {
        this.encryptedSecret = encryptedSecret;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(8);
        out.writeInt(this.encryptedSecret.length);
        out.write(encryptedSecret);
    }
}
