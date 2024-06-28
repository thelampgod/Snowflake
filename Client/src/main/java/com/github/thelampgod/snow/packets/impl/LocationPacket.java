package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.WrappedPacket;

import java.nio.charset.StandardCharsets;

public class LocationPacket extends WrappedPacket {
    private final int groupId;
    private final double x;
    private final double y;
    private final double z;
    private final String dimension;

    public LocationPacket(int groupId, double x, double y, double z, String dimension) {
        this.groupId = groupId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    public LocationPacket(byte[] bytes) {
        String data = new String(bytes).substring(1);
        String[] parts = data.split(DIVIDER);
        this.groupId = Integer.parseInt(parts[0]);
        this.x = Double.parseDouble(parts[1]);
        this.y = Double.parseDouble(parts[2]);
        this.z = Double.parseDouble(parts[3]);
        this.dimension = parts[4];
    }

    @Override
    public byte[] data() {
        String data = "1"; // locationpacket id
        data += groupId;
        data += DIVIDER;
        data += x;
        data += DIVIDER;
        data += y;
        data += DIVIDER;
        data += z;
        data += DIVIDER;
        data += dimension;
        return data.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void handle() {
        Snow.instance.getRenderer().updatePoint(this.getSender(), x, y, z, dimension, this.groupId);
    }
}
