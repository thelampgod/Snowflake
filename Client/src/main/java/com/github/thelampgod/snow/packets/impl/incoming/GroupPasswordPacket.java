package com.github.thelampgod.snow.packets.impl.incoming;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GroupPasswordPacket extends SnowflakePacket {

    private final int groupId;
    private final byte[] encryptedPassword;
    public GroupPasswordPacket(DataInputStream in) throws IOException {
        this.groupId = in.readInt();
        this.encryptedPassword = new byte[in.readInt()];
        in.readFully(this.encryptedPassword);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() {
        final Group group = Snow.instance.getGroupManager().get(groupId);

        try {
            final byte[] password = EncryptionUtil.decrypt(encryptedPassword, Helper.getPrivateKey());
            group.setPassword(password);
        } catch (Exception e) {
            Snow.instance.getLog().error("Failed to decrypt group password: " + e.getMessage(), e);
        }
    }
}
