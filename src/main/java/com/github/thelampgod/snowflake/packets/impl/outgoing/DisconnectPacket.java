package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snowflake.util.Helper.getServer;
import static net.daporkchop.lib.logging.Logging.logger;

public class DisconnectPacket extends SnowflakePacket {
    private final String reason;

    public DisconnectPacket(String reason, SocketClient sender) {
        super(sender);
        this.reason = reason;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(7);
        out.writeUTF(reason);
        getServer().removeClient(this.getSender());
        logger.debug(this.getSender() + " disconnected. Reason: " + reason);
    }

    @Override
    public void handle() throws IOException {
        this.getSender().getConnection().sendPacket(this);
    }
}
