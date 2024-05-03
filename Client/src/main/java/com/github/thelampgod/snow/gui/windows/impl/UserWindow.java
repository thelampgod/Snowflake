package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.users.User;

public class UserWindow extends SnowWindow {
    private final int id;

    public UserWindow(User user) {
        super(user.getName(), false, 200, 100);
        this.id = user.getId();
    }

    public int getId() {
        return id;
    }
}
