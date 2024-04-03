package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.WrappedPacket;

public class MessagePacket extends WrappedPacket {

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

    public static class Group extends MessagePacket {
        public Group(int groupId, String message) throws Exception {
            super(true, groupId, message);
        }

        @Override
        public void handle() {
            //todo:
            //groupscreen.add(message, from, group)
            //super.getSender()

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
