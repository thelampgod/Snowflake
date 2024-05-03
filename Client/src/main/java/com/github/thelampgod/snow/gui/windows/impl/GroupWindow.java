package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class GroupWindow extends SnowWindow {
    private final Group group;

    // Crown character https://graphemica.com/%F0%9F%91%91
    private final static String crown = "\uD83D\uDC51";

    private TextButton addButton;
    private TextButton removeButton;

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
            addButton = new TextButton("+", width - (padding + size) * 2, padding, size, Color.BLACK.getRGB(),
                    7, "Add User to Group");
            removeButton = new TextButton("-", width - (padding + size) * 3, padding, size, Color.BLACK.getRGB(),
                    7, "Remove User from Group");
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        // Render Start Location Share button

        if (group.isOwner()) {
            // Render add and remove user buttons
            addButton.render(ctx, mouseX, mouseY);
            removeButton.render(ctx, mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (group.isOwner()) {
            if (addButton.mouseHover(mouseX, mouseY)) {
                Snow.instance.getOrCreateSnowScreen().focusWindow(new UserAddToGroupListWindow(this.group, 150, 200));
            }
            if (removeButton.mouseHover(mouseX, mouseY)) {
                Snow.instance.getOrCreateSnowScreen().focusWindow(new UserRemoveFromGroupListWindow(this.group, 150, 200));

            }
        }
    }
}
