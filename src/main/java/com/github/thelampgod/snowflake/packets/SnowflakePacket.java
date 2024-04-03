package com.github.thelampgod.snowflake.packets;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.groups.Group;
import com.github.thelampgod.snowflake.packets.impl.*;
import com.github.thelampgod.snowflake.packets.impl.incoming.GroupInvitePacket;
import com.github.thelampgod.snowflake.packets.impl.incoming.*;
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
            case 4:
                return new ListUsersPacket(sender);
            case 6:
                return new KeyRequestPacket(in, sender);
            case 9:
                return new KeepAlivePacket(in, sender);
            case 10:
                return new HandshakeResponsePacket(in, sender);
            case 12:
                return new CreateGroupPacket(in, sender);
            case 13:
                return new GroupInvitePacket(in, sender);
            case 14:
                return new GroupPasswordUpdatePacket(in, sender);
            case 15:
                return new GroupRemovePacket(in, sender);
            case 16:
                return new GroupUserRemovePacket(in, sender);
            case 17:
                return new EncryptedDataPacket(in, sender);
            default:
                return new DisconnectPacket("Unknown packet type " + id, sender);
        }
    }

    public abstract void writeData(DataOutputStream out) throws IOException;

    public void handle() throws IOException {
    }

    public boolean isAuthenticated() throws IOException {
        if (!this.getSender().isAuthenticated()) {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("Not authenticated."));
            return false;
        }
        return true;
    }

    public boolean isOwner(int groupId) throws IOException {
        final Group group = Snowflake.INSTANCE.getGroupManager().get(groupId);
        if (group.getOwnerId() != this.getSender().getId()) {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("You are not the owner of this group."));
            return false;
        }
        return true;
    }
}
