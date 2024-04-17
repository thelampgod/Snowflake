package com.github.thelampgod.snow.users;

import com.google.common.collect.Sets;

import java.util.Set;

public class UserManager {

    private final Set<User> users = Sets.newHashSet();

    public void clear() {
        users.clear();
    }

    public void add(User user) {
        users.add(user);
    }
    public void remove(User user) {
        users.remove(user);
    }

    public void remove(int userId) {
        users.removeIf(user -> user.getId() == userId);
    }

    public User get(int userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findAny()
                .orElseGet(null);
    }
}
