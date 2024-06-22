package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.elements.ButtonListElement;
import com.github.thelampgod.snow.gui.windows.ListWindow;
import com.github.thelampgod.snow.users.User;

import java.util.List;

public class UserListWindow extends ListWindow {
    public UserListWindow(int width, int height) {
        super("Users", width, height, false);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        this.buttonListElement = new ButtonListElement(super.textRenderer, 0, headerHeight, height - headerHeight, width);
        updateButtons();
    }

    @Override
    public void updateButtons() {
        if (!hasInit) return;
        buttonListElement.clearButtons();
        final List<User> users = Snow.instance.getUserManager().getUsers();
        for (final User user : users) {
            if (user.getId() == Snow.instance.getUserManager().getMe()) continue;
            buttonListElement.addButton(
                    user.getName(), 0,
                    () -> Snow.instance.getOrCreateSnowScreen().focusWindow(user)
            );
        }
    }
}
