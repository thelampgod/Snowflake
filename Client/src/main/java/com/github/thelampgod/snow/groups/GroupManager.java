package com.github.thelampgod.snow.groups;

import com.github.thelampgod.snowflake.SocketClient;
import com.google.common.collect.Sets;

import java.util.Set;

public class GroupManager {

    private final Set<Group> groups = Sets.newHashSet();

    public void add(Group group) {
        groups.add(group);
        if(group.getId() == -1) {
            group.setId(groups.size() + 1);
        }
    }

    public void remove(Group group) {
        groups.remove(group);
    }

    public Set<Group> findUserGroups(SocketClient client) {
        return this.findUserGroups(client.getId());
    }

    public Set<Group> findUserGroups(int id) {
        final Set<Group> temp = Sets.newHashSet();
        groups.stream()
                .filter(group -> group.containsUser(id))
                .forEach(temp::add);

        return temp;
    }
}
