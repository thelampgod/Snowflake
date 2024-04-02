package com.github.thelampgod.snowflake.groups;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.database.Database;
import com.github.thelampgod.snowflake.util.DatabaseUtil;
import com.google.common.collect.Sets;

import java.util.Set;

public class GroupManager {

    private final Set<Group> groups = Sets.newHashSet();

    public void add(Group group) {
        groups.add(group);
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

    public Group get(int groupId) {
        return groups.stream()
                .filter(group -> group.getId() == groupId)
                .findAny()
                .orElseGet(null);
    }

    public void load(Database database) {
        groups.addAll(DatabaseUtil.getGroupsFromDb(database));
    }

}
