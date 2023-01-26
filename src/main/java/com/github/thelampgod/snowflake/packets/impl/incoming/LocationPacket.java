package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;

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
        out.writeByte(2);
        out.writeInt(this.getSender().getId());
        out.writeDouble(posX);
        out.writeDouble(posY);
        out.writeDouble(posZ);
        out.writeByte(dimensionId);
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
