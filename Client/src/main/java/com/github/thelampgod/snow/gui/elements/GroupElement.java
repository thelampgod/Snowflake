package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.SnowWindow;
import net.minecraft.client.gui.DrawContext;

public class GroupElement extends SnowWindow {
    private final Group group;
    public GroupElement(Group group) {
        // Crown character https://graphemica.com/%F0%9F%91%91
        super((group.isOwner() ? "\uD83D\uDC51 " : "") + group.getName(), false, 200, 100);
        this.group = group;
    }

    public int getId() {
        return group.getId();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        // Render Start Location Share button

        if (group.isOwner()) {
            // Render add and remove user buttons
        }
    }
}
