package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.gui.SnowWindow;
import com.github.thelampgod.snow.users.User;

public class UserElement extends SnowWindow {
    private final int id;

    public UserElement(User user) {
        super(user.getName(), 200, 100);
        this.id = user.getId();
    }

    public int getId() {
        return id;
    }
}
