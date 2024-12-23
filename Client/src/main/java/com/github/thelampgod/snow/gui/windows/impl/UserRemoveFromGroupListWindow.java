package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.util.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.elements.ButtonListElement;
import com.github.thelampgod.snow.gui.windows.ListWindow;
import com.github.thelampgod.snow.packets.impl.outgoing.GroupUserRemovePacket;
import com.github.thelampgod.snow.users.User;

import java.util.Set;


public class UserRemoveFromGroupListWindow extends ListWindow {
    private final Group group;

    public UserRemoveFromGroupListWindow(Group group, int width, int height) {
        super("Remove from: " + group.getName(), width, height, true);
        this.group = group;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        this.buttonListElement = new ButtonListElement(super.textRenderer, 0, headerHeight, height - headerHeight, width);
        updateButtons();
    }

    @Override
    public void updateButtons() {
        final Set<Integer> users = group.getUsers();
        int j = 0;
        for (final int userId : users) {
            // can't remove yourself
            if (userId == Snow.instance.getUserManager().getMe()) continue;
            final User user = Snow.instance.getUserManager().get(userId);
            if (user == null) {
                buttonListElement.addButton(
                        "UserID:" + userId, 0,
                        () -> {
                            Snow.getServerManager().sendPacket(new GroupUserRemovePacket(group.getId(), userId));
                            Snow.instance.getOrCreateSnowScreen().remove(this);
                        }
                );
            } else {
                buttonListElement.addButton(
                        user.getName(), 0,
                        () -> {
                            Snow.getServerManager().sendPacket(new GroupUserRemovePacket(group.getId(), user.getId()));
                            Snow.instance.getOrCreateSnowScreen().remove(this);
                        }
                );
            }
            ++j;
        }

        if (j == 0) {
            Helper.addToast("No one to remove!");
            Snow.instance.getOrCreateSnowScreen().remove(this);
        }
    }

    public int getGroupId() {
        return group.getId();
    }
}
