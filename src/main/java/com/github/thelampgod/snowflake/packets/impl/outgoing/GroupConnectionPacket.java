package com.github.thelampgod.snowflake.packets.impl.outgoing;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class GroupConnectionPacket extends SnowflakePacket {

    private final int action;
    private final int groupId;
    private final int clientId;


    public GroupConnectionPacket(int action, int groupId, int clientId) throws IOException {
        super(SocketClient.Snowflake());
        this.action = action;
        this.groupId = groupId;
        this.clientId = clientId;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(18);
        out.writeByte(action);
        out.writeInt(groupId);
        out.writeInt(clientId);
    }

    public static class Added extends GroupConnectionPacket {
        public Added(int groupId, int clientId) throws IOException {
            super(0, groupId, clientId);
        }
    }

    public static class Removed extends GroupConnectionPacket {
        public Removed(int groupId, int clientId) throws IOException {
            super(1, groupId, clientId);
        }
    }

    public static class Joined extends GroupConnectionPacket {
        public Joined(int groupId, int clientId) throws IOException {
            super(2, groupId, clientId);
        }
    }

    public static class Left extends GroupConnectionPacket {
        public Left(int groupId, int clientId) throws IOException {
            super(3, groupId, clientId);
        }
    }
}
