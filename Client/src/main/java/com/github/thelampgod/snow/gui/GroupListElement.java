package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GroupListElement extends SnowWindow {

    private final List<GroupButton> buttons = new ArrayList<>();

    public GroupListElement(int width, int height) {
        super("Groups", width, height);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        final List<Group> groups = Snow.instance.getGroupManager().getGroups();
        for (int i = 0; i < groups.size(); ++i) {
            final Group group = groups.get(i);
            buttons.add(new GroupButton(0, headerHeight + 20 * i, width, group.getName(), group.getId(), group.getUsers().size()));
        }

    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        for (GroupButton button : buttons) {
            button.render(ctx, mouseX, mouseY, delta);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        super.mouseClicked(mouseX, mouseY, buttonId);

        for (GroupButton button : buttons) {
            button.mouseClicked(mouseX, mouseY);
        }
    }

    class GroupButton {
        private final int bheight = 17;
        private int bwidth;

        private int bx;
        private int by;
        private final String name;
        private final int id;
        private int size;

        public GroupButton(int x, int y, int w, String name, int id, int size) {
            this.bx = x;
            this.by = y + textRenderer.fontHeight;
            this.bwidth = w;
            this.name = name;
            this.id = id;
            this.size = size;
        }

        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            int color = Color.WHITE.getRGB();
            if (mouseHover(mouseX, mouseY)) {
                color = Color.ORANGE.getRGB();
            }

            // Group name
            ctx.drawTextWithShadow(textRenderer, name, bx + 10,by, color);
            // Group member count
            ctx.drawTextWithShadow(
                    textRenderer,
                    String.valueOf(size),
                    bx + bwidth - textRenderer.getWidth(String.valueOf(size)) - 10,
                    by, Color.GRAY.getRGB()
            );
            // Divider
            ctx.drawHorizontalLine(bx + 5, bwidth - 6, by + 12, color);
            ctx.drawHorizontalLine(bx + 6, bwidth - 5, by + 13, Color.BLACK.getRGB());
        }
        private boolean mouseHover(double mouseX, double mouseY) {
            return mouseX - x > 0 & mouseX - x < bwidth && mouseY - y - by > 0 && mouseY - y - by < bheight;
        }

        public void mouseClicked(double mouseX, double mouseY) {
            if (mouseHover(mouseX,mouseY)) {
                final Group group = Snow.instance.getGroupManager().get(this.id);
                Snow.instance.getOrCreateSnowScreen().focusWindow(group);
            }
        }
    }


}
