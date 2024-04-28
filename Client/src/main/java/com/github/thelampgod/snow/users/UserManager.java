package com.github.thelampgod.snow.users;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

import static com.github.thelampgod.snow.Helper.mc;

public class UserManager {

    private final List<User> users = Lists.newArrayList();
    private int me;

    public UserManager() {
//        users.add(new User("l_amp", 0,null));
//        users.add(new User("Negative_Entropy", 1,null));
//        users.add(new User("munmap", 2,null));
//        users.add(new User("IronException", 3,null));
//        users.add(new User("n0pf0x", 4,null));
    }

    public void clear() {
        users.clear();
    }

    public void add(User user) {
        users.add(user);

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateUserButtons();
    }

    public void remove(User user) {
        users.remove(user.getId());

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateUserButtons();
        screen.removeUserWindow(user);
    }

    public void remove(int userId) {
        final User user = this.get(userId);
        if (this.get(userId) != null) {
            this.remove(user);
        }
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

    public void setMe(int id) {
        this.me = id;
    }

    public int getMe() {
        return me;
    }
}
