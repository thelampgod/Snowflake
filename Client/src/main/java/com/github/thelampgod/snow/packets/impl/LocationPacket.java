package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.packets.WrappedPacket;

public class LocationPacket extends WrappedPacket {
    private final int groupId;
    private final double x;
    private final double y;
    private final double z;
    private final byte dimension;

    public LocationPacket(int groupId, double x, double y, double z, byte dimension) {
        this.groupId = groupId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
    }

    @Override
    public void handle() {
        //todo:
    }
}
