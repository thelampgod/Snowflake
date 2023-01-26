package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;

public class MessagePacket extends SnowflakePacket {
    private final byte[] message;
    private final boolean encrypted;

    public MessagePacket(DataInputStream in, boolean encrypted, SocketClient sender) throws IOException {
        super(sender);
        this.message = new byte[in.readInt()];
        in.readFully(this.message);
        this.encrypted = encrypted;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte((encrypted) ? 3 : 1);
        out.writeInt(this.getSender().getId());
        out.writeInt(message.length);
        out.write(message);
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

    public static class Plain extends MessagePacket {
        public Plain(DataInputStream in, SocketClient sender) throws IOException {
            super(in, false, sender);
        }
    }

    public static class Encrypted extends MessagePacket {
        public Encrypted(DataInputStream in, SocketClient sender) throws IOException {
            super(in, true, sender);
        }
    }
}
