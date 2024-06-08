package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.packets.WrappedPacket;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.github.thelampgod.snow.Helper.mc;

public class MessagePacket extends WrappedPacket {

    // Meant for group or user
    private final boolean group;
    // The group or user id
    private final int id;
    protected final String message;

    public MessagePacket(boolean group, int id, String message) {
        this.group = group;
        this.id = id;
        this.message = message;
    }

    public static MessagePacket fromBytes(byte[] bytes) {
        String data = new String(bytes).substring(1);
        String[] parts = data.split(DIVIDER);
        boolean group = Objects.equals(parts[0], "1");
        int id = Integer.parseInt(parts[1]);
        String message = parts[2];

        if (!group) {
            return new MessagePacket.User(id, message);
        }

        return new MessagePacket.Group(id, message);
    }

    @Override
    public byte[] data() {
        String data = "0"; // messagepacket id
        data += group ? 1 : 0;
        data += DIVIDER;
        data += id;
        data += DIVIDER;
        data += message;
        return data.getBytes(StandardCharsets.UTF_8);
    }

    public static class Group extends MessagePacket {
        public Group(int groupId, String message) {
            super(true, groupId, message);
        }

        @Override
        public void handle() {
            Snow.instance.getOrCreateSnowScreen().addMessage(super.id, super.getSender(), this.message);
            if (mc.currentScreen instanceof SnowScreen) return;
            Helper.addToast("New message!", Snow.instance.getUserManager().get(super.getSender()).getName() + " says...");
        }
    }

    public static class User extends MessagePacket {
        public User(int userId, String message) {
            super(false, userId, message);
        }

        @Override
        public void handle() {
            Snow.instance.getOrCreateSnowScreen().addMessage(super.getSender(), this.message);
            if (mc.currentScreen instanceof SnowScreen) return;
            Helper.addToast("New message!", Snow.instance.getUserManager().get(super.getSender()).getName() + " says...");
        }
    }
}
