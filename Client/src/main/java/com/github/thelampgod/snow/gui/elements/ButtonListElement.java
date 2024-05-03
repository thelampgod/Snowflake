package com.github.thelampgod.snow.gui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ButtonListElement {
    private final int x;
    private final int y;
    private final int height;
    private final int width;
    private final List<ListButton> buttons = new ArrayList<>();

    private final TextRenderer textRenderer;

    private int scrollPosition = 0;

    public ButtonListElement(TextRenderer textRenderer, int x, int y, int height, int width) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void preRender(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y + scrollPosition * 20, 0);
        render(ctx, mouseX, mouseY, delta);
        ctx.getMatrices().pop();
    }

    private void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        for (ListButton button : buttons) {
            int buttonY = (button.index + scrollPosition) * 20 + y;
            if (buttonY > height || buttonY < y) continue;
            button.render(ctx, mouseX, mouseY, delta);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        for (ListButton button : buttons) {
            button.mouseClicked(mouseX, mouseY);
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInElement(mouseX, mouseY)) return;
        int buttonsHeight = (buttons.size()) * 20;
        if (buttonsHeight < height) return;

        scrollPosition += (verticalAmount > 0 ? 1 : -1);
        //find how much height needs to be cut off
        int min = (buttonsHeight - (height)) / 20;
        scrollPosition = MathHelper.clamp(scrollPosition, -min, 0);
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    public void clearButtons() {
        buttons.clear();
    }

    public void addButton(int width, String text, int size, Runnable runnable) {
        buttons.add(new ListButton(0, buttons.size(), width, text, size, runnable));
    }


    protected class ListButton {
        private final int bheight = 17;
        private int bwidth;

        private int bx;

        private int index;
        private int by;
        private final String name;
        private int size;

        private final Runnable onClick;

        public ListButton(int x, int index, int w, String name, int size, Runnable onClick) {
            this.bx = x;
            this.index = index;
            this.by = index * 20 + textRenderer.fontHeight;
            this.bwidth = w;
            this.name = name;
            this.size = size;
            this.onClick = onClick;
        }

        public ListButton(int x, int y, int w, String name, Runnable onClick) {
            this(x, y, w, name, 0, onClick);
        }

        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            int color = Color.WHITE.getRGB();
            if (mouseHover(mouseX, mouseY)) {
                color = Color.ORANGE.getRGB();
            }

            // Name
            ctx.drawTextWithShadow(textRenderer, name, bx + 10, by, color);
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
        }

        private boolean mouseHover(double mouseX, double mouseY) {
            mouseY -= scrollPosition * 20;
            return mouseX - x > 0 & mouseX - x < bwidth && mouseY - y - by > 0 && mouseY - y - by < bheight;
        }

        public void mouseClicked(double mouseX, double mouseY) {
            if (mouseHover(mouseX, mouseY)) {
                onClick.run();
            }
        }
    }
}
