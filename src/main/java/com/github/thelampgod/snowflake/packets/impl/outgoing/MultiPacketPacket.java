package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Queue;

public class MultiPacketPacket extends SnowflakePacket {
    private final Queue<SnowflakePacket> packets;
    public MultiPacketPacket(Queue<SnowflakePacket> packets) throws IOException {
        super(SocketClient.Snowflake());
        this.packets = packets;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(14);
        while (!packets.isEmpty()) {
            out.writeBoolean(false); //tell client if its the last or not
            SnowflakePacket packet = packets.poll();
            packet.writeData(out);
        }
        out.writeBoolean(true);
    }
}
