package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.SnowWindow;

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
