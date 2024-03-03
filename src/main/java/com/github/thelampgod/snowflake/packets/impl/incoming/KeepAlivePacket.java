package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.DisconnectPacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;
import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class KeepAlivePacket extends SnowflakePacket {
    private final long timestamp;

    public KeepAlivePacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.timestamp = in.readLong();
    }

    public KeepAlivePacket(long timestamp) throws IOException {
        super(SocketClient.Snowflake());
        this.timestamp = timestamp;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(4);
        out.writeLong(timestamp);
    }

    @Override
    public void handle() throws IOException {
        long now = 0;
        Optional<SocketClient> receiver = getConnectedClients().stream()
                .filter(SocketClient::isReceiver)
                .filter(c -> c.getLinkString().equals(this.getSender().getLinkString())).findAny();

        if (receiver.isPresent()) {
            now = receiver.get().getNow();
        } else {

            this.getSender().getConnection().sendPacket(new DisconnectPacket("No receiver?", this.getSender()));
        }

        if (this.timestamp != now) {
            this.getSender().getConnection().sendPacket(new DisconnectPacket(String.format("Failed keepalive, expected %d, got %d", now, timestamp), this.getSender()));
            return;
        }
        receiver.get().responded = true;
        getLog().debug(this.getSender() + " keepalive response in " + (System.currentTimeMillis() - now) + "ms");

    }
}
