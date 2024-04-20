package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.groups.Group;

public class GroupElement extends SnowWindow {
    private final int id;
    public GroupElement(Group group) {
        super(group.getName(), 200, 100);
        this.id = group.getId();
    }

    public int getId() {
        return id;
    }
}
