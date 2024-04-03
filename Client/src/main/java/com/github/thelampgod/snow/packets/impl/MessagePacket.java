package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class MessagePacket extends SnowflakePacket {

    // Meant for group or user
    private final boolean group;
    // The group or user id
    private final int id;
    private final String message;
    private final int sender;

    public MessagePacket(boolean group, int id, String message) {
        this.group = group;
        this.id = id;
        this.message = message;
        // unused for sending
        this.sender = -1;
    }

    public MessagePacket(boolean group, DataInputStream in) throws IOException {
        this.group = group;
        this.sender = in.readInt();
        this.id = in.readInt();
        this.message = in.readUTF();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        throw new UnsupportedOperationException("MessagePacket should be wrapped in an EncryptedDataPacket");
//        out.writeByte(18);
//        out.writeBoolean(group);
//        out.writeInt(id);
//        out.writeUTF(message);
    }

    @Override
    public void handle() {

    }

    public static class Group extends MessagePacket {
        public Group(int groupId, String message) throws Exception {
            super(true, groupId, message);
        }

        public Group(DataInputStream in) throws IOException {
            super(true, in);
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

        public User(DataInputStream in) throws IOException {
            super(false, in);
        }

        @Override
        public void handle() {
            //todo:

        }
    }
}
