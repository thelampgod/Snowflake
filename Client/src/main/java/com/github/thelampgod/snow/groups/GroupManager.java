package com.github.thelampgod.snow.groups;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class GroupManager {

    private final List<Group> groups = Lists.newArrayList();

    public GroupManager() {
        //testing
//        groups.add(new Group("TestGroup", 0, false, Sets.newHashSet(1,2,3,4,5)));
//        groups.add(new Group("Lamp's Group", 1, false, Sets.newHashSet(1,2,4,5)));
//        groups.add(new Group("Entropy Group", 2, false, Sets.newHashSet(1,2,3,4,5)));
//        groups.add(new Group("Epic Group", 3, true, Sets.newHashSet(1,2)));
//        groups.add(new Group(":D", 4, false, Sets.newHashSet(1,5)));

    }

    public void clear() {
        groups.clear();
    }

    public void add(Group group) {
        groups.add(group);

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateGroupButtons();
        if (group.isOwner()) {
            screen.focusWindow(group);
        }
    }

    public void remove(Group group) {
        groups.remove(group);

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateGroupButtons();
        screen.removeGroupWindow(group);
    }

    public Group get(int groupId) {
        return groups.stream()
                .filter(group -> group.getId() == groupId)
                .findAny()
                .orElseGet(null);
    }

    public List<Group> getGroups() {
        return groups;
    }

}
