package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.users.User;

public class UserWindow extends ChatWindow {

    public UserWindow(User user) {
        super(user.getId(), false, user.getName(), 200, 100);
    }
}
