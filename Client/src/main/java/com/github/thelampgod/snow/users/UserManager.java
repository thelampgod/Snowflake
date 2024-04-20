package com.github.thelampgod.snow.users;

import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class UserManager {

    private final List<User> users = Lists.newArrayList();

    public UserManager() {
        users.add(new User("l_amp", 0,null));
        users.add(new User("Negative_Entropy", 1,null));
        users.add(new User("munmap", 2,null));
        users.add(new User("IronException", 3,null));
        users.add(new User("n0pf0x", 4,null));
    }

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

    public List<User> getUsers() {
        return users;
    }
}
