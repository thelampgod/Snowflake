package com.github.thelampgod.snowflake.packets;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.impl.incoming.*;
import com.github.thelampgod.snowflake.packets.impl.outgoing.DisconnectPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SnowflakePacket {
    private final SocketClient sender;

    public SnowflakePacket(SocketClient sender) {
        this.sender = sender;
    }

    public SocketClient getSender() {
        return sender;
    }

    public static SnowflakePacket fromId(byte id, DataInputStream in, SocketClient sender) throws IOException {
        switch (id) {
            case 0:
                return new LoginPacket(in, sender);
            case 1:
                return new MessagePacket.Plain(in, sender);
            case 2:
                return new AddRecipientPacket(in, sender);
            case 3:
                return new RemoveRecipientPacket(in, sender);
            case 4:
                return new ListUsersPacket(sender);
            case 5:
                return new ListRecipientsPacket(sender);
            case 6:
                return new KeyRequestPacket(in, sender);
            case 7:
                return new MessagePacket.Encrypted(in, sender);
            case 8:
                return new LocationPacket(in, sender);
            case 9:
                return new KeepAlivePacket(in, sender);
            case 10:
                return new HandshakeResponsePacket(in, sender);
            case 11:
                return new ChunkPacket(in, sender);
            case 12:
                return new CreateGroupPacket(in, sender);
            default:
                return new DisconnectPacket("Unknown packet type " + id, sender);
        }
    }

    public abstract void writeData(DataOutputStream out) throws IOException;

    public void handle() throws IOException {
        if (!this.getSender().isAuthenticated()) {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("Not authenticated."));
        }
    }
}
