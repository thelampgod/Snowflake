package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import net.minecraft.client.gui.DrawContext;

public class GroupWindow extends SnowWindow {
    private final Group group;

    // Crown character https://graphemica.com/%F0%9F%91%91
    private final static String crown = "\uD83D\uDC51";

    public GroupWindow(Group group) {
        super((group.isOwner() ? crown + " " : "") + group.getName(), false, 200, 100);
        this.group = group;
    }

    public int getId() {
        return group.getId();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        if (group.isOwner()) {
            addHeaderButton("+", 7, "Add User to Group",
                    () -> Snow.instance.getOrCreateSnowScreen().focusWindow(getAddWindow(this.group)));
            addHeaderButton("-", 7, "Remove User from Group",
                    () -> Snow.instance.getOrCreateSnowScreen().focusWindow(getRemoveWindow(this.group)));
        }
    }

    private UserRemoveFromGroupListWindow getRemoveWindow(Group group) {
        for (SnowWindow w : SnowScreen.windowList) {
            if (w instanceof UserRemoveFromGroupListWindow listWindow) {
                if (listWindow.getGroupId() == group.getId()) {
                    return listWindow;
                }
            }
        }

        return new UserRemoveFromGroupListWindow(this.group, 150, 200);
    }

    private UserAddToGroupListWindow getAddWindow(Group group) {
        for (SnowWindow w : SnowScreen.windowList) {
            if (w instanceof UserAddToGroupListWindow listWindow) {
                if (listWindow.getGroupId() == group.getId()) {
                    return listWindow;
                }
            }
        }

        return new UserAddToGroupListWindow(this.group, 150, 200);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        // Render Start Location Share button
    }
}
