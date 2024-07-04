package com.github.thelampgod.snowflake.groups;

import com.github.thelampgod.snowflake.SocketClient;
import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class Group {

    private final String name;
    private int groupId = -1;
    private int ownerId;

    private final Set<Integer> users = Sets.newHashSet();

    public Group(String name, int groupId, int ownerId) {
        this.name = name;
        this.groupId = groupId;
        this.ownerId = ownerId;
        users.add(ownerId);
    }

    public Group(String name, int ownerId) {
        this.name = name;
        this.ownerId = ownerId;
        users.add(ownerId);
    }

    public int getId() {
        return groupId;
    }

    public void setId(int groupId) {
        this.groupId = groupId;
    }

    public void setOwner(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void addUser(SocketClient client) {
        this.addUser(client.getId());
    }

    public void addUser(int id) {
        users.add(id);
    }

    public void removeUser(SocketClient client) {
        this.removeUser(client.getId());
    }

    public void removeUser(int id) {
        users.remove(id);
    }

    public boolean containsUser(int id) {
        return users.contains(id);
    }

    public Set<Integer> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupId == group.groupId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId);
    }
}
