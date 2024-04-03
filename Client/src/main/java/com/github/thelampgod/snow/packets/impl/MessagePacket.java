package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataOutputStream;
import java.io.IOException;

public class MessagePacket extends SnowflakePacket {

    // Meant for group or user
    private final boolean group;
    // The group or user id
    private final int id;
    private final String message;

    public MessagePacket(boolean group, int id, String message) {
        this.group = group;
        this.id = id;
        this.message = message;
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        throw new UnsupportedOperationException("MessagePacket cannot be sent on its own, should be wrapped in an EncryptedDataPacket");
    }

    @Override
    public void handle() {

    }

    public static class Group extends MessagePacket {
        public Group(int groupId, String message) throws Exception {
            super(true, groupId, message);
        }

        @Override
        public void handle() {
            //todo:
            //groupscreen.add(message, from, group)

        }
    }

    public static class User extends MessagePacket {
        public User(int userId, String message) {
            super(false, userId, message);
        }

        @Override
        public void handle() {
            //todo:

        }
    }
}
