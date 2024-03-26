package com.github.thelampgod.snow.groups;

import java.util.Set;

public class Group {

    private final String name;
    private final int groupId;

    private boolean isOwner;
    private final Set<Integer> users;
    private byte[] password;

    public Group(String name, int groupId, boolean isOwner, Set<Integer> users) {
        this.name = name;
        this.groupId = groupId;
        this.isOwner = isOwner;
        this.users = users;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public int getId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public void addUser(int id) {
        users.add(id);
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
}
