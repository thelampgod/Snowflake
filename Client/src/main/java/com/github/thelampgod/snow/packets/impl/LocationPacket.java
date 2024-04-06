package com.github.thelampgod.snow.packets.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.WrappedPacket;
import com.github.thelampgod.snow.render.WaypointRenderer;
import com.github.thelampgod.snow.users.User;

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
        Snow.instance.getRenderer().updatePoint(this.getSender(), x, y, z, dimension, this.groupId);
    }
}
