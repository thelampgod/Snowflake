package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.SnowflakePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class EncryptedDataPacket extends SnowflakePacket {
    private final int groupId;
    private final byte[] encryptedPacket;

    public EncryptedDataPacket(int groupId, SnowflakePacket packet) throws Exception {
        this.groupId = groupId;
        this.encryptedPacket = EncryptionUtil.encryptPacket(packet, groupId);
    }

    public EncryptedDataPacket(DataInputStream in) throws IOException {
        this.groupId = in.readInt();
        this.encryptedPacket = new byte[in.readInt()];
        in.readFully(encryptedPacket);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {
        out.writeByte(17);
        out.writeInt(groupId);
        out.writeInt(encryptedPacket.length);
        out.write(encryptedPacket);
    }

    @Override
    public void handle() {
        final Group group = Snow.instance.getGroupManager().get(groupId);

        try {
            byte[] decrypted = EncryptionUtil.decryptByPassword(encryptedPacket, group.getPassword());

            SnowflakePacket packet = EncryptionUtil.toPacket(decrypted);
            packet.handle();
        } catch (Exception e) {
            printModMessage("Failed to decrypt");
            e.printStackTrace();
        }
    }
}
