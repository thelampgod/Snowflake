package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class RecipientsPacket extends SnowflakePacket {
    private final Set<Integer> recipients;

    public RecipientsPacket(Set<Integer> recipients) throws IOException {
        super(SocketClient.Snowflake());
        this.recipients = recipients;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(11);
        out.writeByte(recipients.size());
        for (int id : recipients) {
            out.writeInt(id);
        }
    }

    @Override
    public void handle() throws IOException {

    }
}
