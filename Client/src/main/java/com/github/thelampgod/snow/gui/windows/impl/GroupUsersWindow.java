package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.elements.ButtonListElement;
import com.github.thelampgod.snow.gui.windows.ListWindow;
import com.github.thelampgod.snow.users.User;

import java.util.Set;

public class GroupUsersWindow extends ListWindow {
    private Set<Integer> users;

    public GroupUsersWindow(String name, Set<Integer> users) {
        super(name + " Users", 150, 210, true);
        this.users = users;

    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        this.buttonListElement = new ButtonListElement(super.textRenderer, 0, headerHeight, height - headerHeight, width);
        updateButtons();
    }

    @Override
    public void updateButtons() {
        for (final int userId : users) {
            final User user = Snow.instance.getUserManager().get(userId);
            if (user == null) {
                buttonListElement.addButton(
                        "UserID:" + userId, 0,
                        () -> {
                        }
                );
                return;
            }

            buttonListElement.addButton(
                    user.getName(), 0,
                    () -> {
                    }
            );
        }
    }
}
