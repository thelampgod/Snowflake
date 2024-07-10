package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class GroupPasswordPacket extends SnowflakePacket {

    private final int groupId;
    private final byte[] password;
    public GroupPasswordPacket(int groupId, byte[] decryptKey) {
        this.groupId = groupId; this.password = decryptKey;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(17);
        out.writeInt(groupId);
        out.writeInt(password.length);
        out.write(password);
    }
}
