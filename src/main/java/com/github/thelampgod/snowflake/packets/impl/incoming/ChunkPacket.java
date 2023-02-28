package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;

public class ChunkPacket extends SnowflakePacket {
    private final int chunkX;
    private final int chunkZ;
    private final int availableSections;
    private final byte[] buffer;

    public ChunkPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.chunkX = in.readInt();
        this.chunkZ = in.readInt();
        this.availableSections = in.readInt();
        this.buffer = new byte[in.readInt()];
        in.readFully(this.buffer);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(15);
        out.writeInt(this.chunkX);
        out.writeInt(this.chunkZ);
        out.writeInt(this.availableSections);
        out.writeInt(this.buffer.length);
        out.write(this.buffer);
    }

    @Override
    public void handle() throws IOException {
        super.handle();

        Set<Integer> recipients = this.getSender().getConnection().recipientsIds;
        for (SocketClient receiver : getConnectedClients()) {
            if (!recipients.contains(receiver.getId())) continue;
            if (!receiver.isReceiver()) continue;

            receiver.getConnection().sendPacket(this);
        }
    }
}
