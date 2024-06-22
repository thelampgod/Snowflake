package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.WrappedPacket;
import com.github.thelampgod.snow.packets.impl.outgoing.GroupLeavePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class EncryptedDataPacket extends SnowflakePacket {

    // Meant for group or user
    private final boolean group;

    private int senderId;
    // The group or user id
    private final int id;
    private final byte[] encryptedPacket;

    public EncryptedDataPacket(boolean group, int id, WrappedPacket packet) throws Exception {
        this.group = group;
        this.id = id;
        this.encryptedPacket = EncryptionUtil.encryptPacket(packet, group, id);
    }

    public EncryptedDataPacket(boolean group, DataInputStream in) throws IOException {
        this.group = group;
        this.senderId = in.readInt();
        this.id = in.readInt();
        this.encryptedPacket = new byte[in.readInt()];
        in.readFully(encryptedPacket);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(17);
        out.writeBoolean(group);
        out.writeInt(id);
        out.writeInt(encryptedPacket.length);
        out.write(encryptedPacket);
    }

    @Override
    public void handle() {

    }

    public static class Group extends EncryptedDataPacket {
        public Group(int groupId, WrappedPacket packet) throws Exception {
            super(true, groupId, packet);
        }

        public Group(DataInputStream in) throws IOException {
            super(true, in);
        }

        @Override
        public void handle() {
            final com.github.thelampgod.snow.groups.Group group = Snow.instance.getGroupManager().get(super.id);

            try {
                byte[] decrypted = EncryptionUtil.decryptByPassword(super.encryptedPacket, group.getPassword());

                WrappedPacket packet = EncryptionUtil.toPacket(decrypted);
                packet.setSender(super.senderId);
                packet.handle();
            } catch (Exception e) {
                Snow.instance.getLog().error("Failed to decrypt (outdated password?) - leaving group: " + e.getMessage(), e);
                Snow.getServerManager().sendPacket(new GroupLeavePacket(group.getId()));
                e.printStackTrace();
            }
        }
    }

    public static class User extends EncryptedDataPacket {
        public User(int userId, WrappedPacket packet) throws Exception {
            super(false, userId, packet);
        }

        public User(DataInputStream in) throws IOException {
            super(false, in);
        }

        @Override
        public void handle() {
            try {
                byte[] decrypted = EncryptionUtil.decrypt(super.encryptedPacket, Helper.getPrivateKey());

                WrappedPacket packet = EncryptionUtil.toPacket(decrypted);
                packet.setSender(super.senderId);
                packet.handle();
            } catch (Exception e) {
                printModMessage("Failed to decrypt");
                e.printStackTrace();
            }
        }
    }
}
