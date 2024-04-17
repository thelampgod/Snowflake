package com.github.thelampgod.snow.groups;

import com.google.common.collect.Sets;

import java.util.Set;

public class GroupManager {

    private final Set<Group> groups = Sets.newHashSet();

    public void clear() {
        groups.clear();
    }

    public void add(Group group) {
        groups.add(group);
    }
    public void remove(Group group) {
        groups.remove(group);
    }

    public Group get(int groupId) {
        return groups.stream()
                .filter(group -> group.getId() == groupId)
                .findAny()
                .orElseGet(null);
    }

}
