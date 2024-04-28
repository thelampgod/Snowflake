package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.users.User;

import java.util.List;

public class UserListElement extends ListElement {
    public UserListElement(int width, int height) {
        super("Users", width, height);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        updateButtons();
    }

    public void updateButtons() {
        buttons.clear();
        final List<User> users = Snow.instance.getUserManager().getUsers();
        for (int i = 0; i < users.size(); ++i) {
            final User user = users.get(i);
            buttons.add(
                    new ListButton(
                            0,
                            headerHeight + 20 * i,
                            width, user.getName(),
                            () -> Snow.instance.getOrCreateSnowScreen().focusWindow(user)
                    ));
        }
    }
}
