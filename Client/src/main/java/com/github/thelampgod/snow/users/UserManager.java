package com.github.thelampgod.snow.users;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class UserManager {

    private final List<User> users = Lists.newArrayList();
    private int me;

    public void clear() {
        users.clear();
    }

    public boolean add(User user) {
        users.add(user);
        if (user.getId() == me) return false;

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateUserButtons();
        return true;
    }

    public void remove(User user) {
        users.remove(user);

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateUserButtons();
        screen.removeUserWindow(user);
    }

    public void remove(int userId) {
        final User user = this.get(userId);
        if (user != null) {
            this.remove(user);
        }
    }

    public User get(int userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findAny()
                .orElse(null);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setMe(int id) {
        this.me = id;
    }

    public int getMe() {
        return me;
    }

    public User getMeUser() {
        return get(me);
    }
}
