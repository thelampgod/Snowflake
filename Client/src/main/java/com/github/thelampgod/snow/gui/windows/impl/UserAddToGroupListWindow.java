package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.util.EncryptionUtil;
import com.github.thelampgod.snow.util.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.elements.ButtonListElement;
import com.github.thelampgod.snow.gui.windows.ListWindow;
import com.github.thelampgod.snow.packets.impl.outgoing.GroupInvitePacket;
import com.github.thelampgod.snow.users.User;

import java.util.List;

import static com.github.thelampgod.snow.util.Helper.printModMessage;

public class UserAddToGroupListWindow extends ListWindow {
    private final Group group;

    public UserAddToGroupListWindow(Group group, int width, int height) {
        super("Add to: " + group.getName(), width, height, true);
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
        final List<User> users = Snow.instance.getUserManager().getUsers();
        int j = 0;
        for (final User user : users) {
            if (group.containsUser(user.getId())) continue;
            buttonListElement.addButton(
                    user.getName(), 0,
                    () -> {
                        sendInvite(user, group);
                        Snow.instance.getOrCreateSnowScreen().remove(this);
                    }
            );
            ++j;
        }

        if (j == 0) {
            Helper.addToast("No one to add!");
            Snow.instance.getOrCreateSnowScreen().remove(this);
        }
    }

    private void sendInvite(User user, Group group) {
        try {
            byte[] encryptedGroupPassword = EncryptionUtil.encrypt(group.getPassword(), user.getKey());
            Snow.getServerManager().sendPacket(new GroupInvitePacket(user.getId(), group.getId(), encryptedGroupPassword));
        } catch (Exception e) {
            printModMessage("Failed to send group password");
            e.printStackTrace();
        }
    }

    public int getGroupId() {
        return group.getId();
    }
}
