package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class GroupPasswordUpdatePacket extends SnowflakePacket {

    private final int groupId;
    private final byte[] newPassword;

    public GroupPasswordUpdatePacket(int groupId, byte[] newPassword) {
        this.groupId = groupId;
        this.newPassword = newPassword;
    }

    public GroupPasswordUpdatePacket(DataInputStream in) throws IOException {
        this.groupId = in.readInt();
        this.newPassword = new byte[in.readInt()];
        in.readFully(newPassword);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(14);
        out.writeInt(groupId);
        out.writeInt(newPassword.length);
        out.write(newPassword);
    }

    @Override
    public void handle() {
        final Group group = Snow.instance.getGroupManager().get(groupId);

        try {
            final byte[] password = EncryptionUtil.decryptByPassword(newPassword, group.getPassword());
            group.setPassword(password);
        } catch (Exception e) {
            printModMessage("Couldn't decrypt");
            e.printStackTrace();
        }
    }
}
