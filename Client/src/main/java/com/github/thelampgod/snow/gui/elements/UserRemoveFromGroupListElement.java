package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.impl.outgoing.GroupUserRemovePacket;
import com.github.thelampgod.snow.users.User;

import java.util.Set;


public class UserRemoveFromGroupListElement extends ListElement {
    private final Group group;
    public UserRemoveFromGroupListElement(Group group, int width, int height) {
        super("Remove from: " + group.getName(), width, height, true);
        this.group = group;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        final Set<Integer> users = group.getUsers();
        int j = 0;
        for (final int userId : users) {
            final User user = Snow.instance.getUserManager().get(userId);
            buttons.add(
                    new ListButton(
                            0,
                            headerHeight + 20 * j,
                            width, user.getName(),
                            () -> {
                                Snow.getServerManager().sendPacket(new GroupUserRemovePacket(group.getId(), user.getId()));
                                Snow.instance.getOrCreateSnowScreen().remove(this);
                            }
                    ));
            ++j;
        }

        if (j == 0) {
            Helper.addToast("No one to remove!");
            Snow.instance.getOrCreateSnowScreen().remove(this);
        }
    }
}
