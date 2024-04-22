package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;

import java.util.List;

public class GroupListElement extends ListElement {

    public GroupListElement(int width, int height) {
        super("Groups", width, height);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        final List<Group> groups = Snow.instance.getGroupManager().getGroups();
        for (int i = 0; i < groups.size(); ++i) {
            final Group group = groups.get(i);
            buttons.add(new ListButton(0, headerHeight + 20 * i, width, group.getName(), group.getId(), group.getUsers().size()));
        }
    }
}
