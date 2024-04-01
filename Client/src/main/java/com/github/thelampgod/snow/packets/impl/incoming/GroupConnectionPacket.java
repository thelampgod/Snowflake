package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;
import com.github.thelampgod.snow.packets.impl.GroupPasswordUpdatePacket;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class GroupConnectionPacket extends SnowflakePacket {
    protected final int groupId;
    protected final int clientId;
    public GroupConnectionPacket(DataInputStream in) throws IOException {
        this.groupId = in.readInt();
        this.clientId = in.readInt();
    }


    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {
        //todo
    }

    public static class Added extends GroupConnectionPacket {
        public Added(DataInputStream in) throws IOException {
            super(in);
        }

        @Override
        public void handle() {
            final Group group = Snow.instance.getGroupManager().get(groupId);
            group.addUser(clientId);
        }
    }

    public static class Removed extends GroupConnectionPacket {
        public Removed(DataInputStream in) throws IOException {
            super(in);
        }

        @Override
        public void handle() {
            final Group group = Snow.instance.getGroupManager().get(groupId);
            group.removeUser(clientId);

            try {
                if (group.isOwner()) {
                    final String newPassword = RandomStringUtils.random(16, 0, 0, true, true, null, new SecureRandom());
                    byte[] encrypted = EncryptionUtil.encryptByPassword(newPassword.getBytes(), group.getPassword());
                    Snow.getServerManager().sendPacket(new GroupPasswordUpdatePacket(groupId, encrypted));
                }
            } catch (Exception e) {
                printModMessage("Failed to encrypt");
                e.printStackTrace();
            }
        }
    }

    public static class Joined extends GroupConnectionPacket {
        public Joined(DataInputStream in) throws IOException {
            super(in);
        }

        @Override
        public void handle() {
            //todo
        }
    }

    public static class Left extends GroupConnectionPacket {
        public Left(DataInputStream in) throws IOException {
            super(in);
        }

        @Override
        public void handle() {
            //todo
        }
    }
}