package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.google.common.collect.Sets;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.Set;

public class MultiPacketPacket extends SnowflakePacket {
    private final Queue<SnowflakePacket> packets;
    public MultiPacketPacket(Queue<SnowflakePacket> packets) {
        this.packets = packets;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(14);
        while (!packets.isEmpty()) {
            out.writeBoolean(false); //tell client if it is the last or not
            SnowflakePacket packet = packets.poll();
            packet.writeData(out);
        }
        out.writeBoolean(true);
    }

    public Set<SnowflakePacket> getPackets() {
        return Sets.newHashSet(packets);
    }
}
