package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.gui.SnowWindow;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ListElement extends SnowWindow {

    protected final List<ListButton> buttons = new ArrayList<>();
    double scrollPosition = 0;

    public ListElement(String title, int width, int height) {
        this(title, width, height, false);
    }

    public ListElement(String title, int width, int height, boolean closeable) {
        super(title, width, height, closeable);
        scrollPosition = Math.max(buttons.size() - 1, 0);
    }

    @Override
    public void init(int width, int height) {
        int maxWidth = 0;
        for (ListButton button : buttons) {
            int buttonWidth = textRenderer.getWidth(button.name) + 10;
            if (maxWidth < buttonWidth) {
                maxWidth = buttonWidth;
            }
        }

        super.init(Math.max(maxWidth, width), height);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        for (ListButton button : buttons) {
            button.render(ctx, mouseX, mouseY, delta, scrollPosition);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        super.mouseClicked(mouseX, mouseY, buttonId);

        for (ListButton button : buttons) {
            button.mouseClicked(mouseX, mouseY, scrollPosition);
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInWindow(mouseX, mouseY)) return;
        int buttonsHeight = headerHeight + (buttons.size() + 1) * 20;
        if (buttonsHeight < height) return;

        scrollPosition += (verticalAmount > 0 ? 1 : -1);
        //find how much height needs to be cut off
        int min = (buttonsHeight - (height - headerHeight - 20)) / 20;
        scrollPosition = MathHelper.clamp(scrollPosition, -min, 0);
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    protected class ListButton {
        private final int bheight = 17;
        private int bwidth;

        private int bx;
        private int by;
        private final String name;
        private int size;

        private final Runnable onClick;

        public ListButton(int x, int y, int w, String name, int size, Runnable onClick) {
            this.bx = x;
            this.by = y + textRenderer.fontHeight;
            this.bwidth = w;
            this.name = name;
            this.size = size;
            this.onClick = onClick;
        }

        public ListButton(int x, int y, int w, String name, Runnable onClick) {
            this(x,y,w,name,0, onClick);
        }

        public void render(DrawContext ctx, int mouseX, int mouseY, float delta, double scrollPosition) {
            int tempY = by;
            by += (int) (scrollPosition * 20);
            if (by < headerHeight + 20 || by > height) {
                by = tempY;
                return;
            }

            int color = Color.WHITE.getRGB();
            if (mouseHover(mouseX, mouseY)) {
                color = Color.ORANGE.getRGB();
            }

            // Name
            ctx.drawTextWithShadow(textRenderer, name, bx + 10,by, color);
            if (size != 0) {
                // Group member count
                ctx.drawTextWithShadow(
                        textRenderer,
                        String.valueOf(size),
                        bx + bwidth - textRenderer.getWidth(String.valueOf(size)) - 10,
                        by, Color.GRAY.getRGB()
                );
            }
            // Divider
            ctx.drawHorizontalLine(bx + 5, bwidth - 6, by + 12, color);
            ctx.drawHorizontalLine(bx + 6, bwidth - 5, by + 13, Color.BLACK.getRGB());
            by = tempY;
        }
        private boolean mouseHover(double mouseX, double mouseY) {
            return mouseX - x > 0 & mouseX - x < bwidth && mouseY - y - by > 0 && mouseY - y - by < bheight;
        }

        public void mouseClicked(double mouseX, double mouseY, double scrollPosition) {
            mouseY -= scrollPosition * 20;
            if (mouseHover(mouseX,mouseY)) {
                onClick.run();
            }
        }
    }


}
