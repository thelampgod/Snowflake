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
    private int height;
    private int width;
    private final List<ListButton> buttons = new ArrayList<>();

    private final TextRenderer textRenderer;

    private int scrollPosition = 0;

    private static final int BUTTON_HEIGHT = 17;
    private static final int PADDING = 3;

    public ButtonListElement(TextRenderer textRenderer, int x, int y, int height, int width) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int yPosition = this.y + textRenderer.fontHeight;
        for (int i = scrollPosition; i < buttons.size(); ++i) {
            if (yPosition + BUTTON_HEIGHT + PADDING >= this.y + textRenderer.fontHeight + this.height) {
                break; // Stop rendering if we've gone past the visible area
            }
            ListButton button = buttons.get(i);
            button.render(ctx, mouseX, mouseY, this.x, yPosition);
            yPosition += BUTTON_HEIGHT + PADDING;
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        int yPosition = this.y + textRenderer.fontHeight;
        for (int i = scrollPosition; i < buttons.size(); ++i) {
            if (yPosition + BUTTON_HEIGHT + PADDING >= this.y + textRenderer.fontHeight + this.height) {
                break; // Stop processing if we've gone past the visible area
            }

            ListButton button = buttons.get(i);
            button.mouseClicked(mouseX, mouseY, yPosition);
            yPosition += BUTTON_HEIGHT + PADDING;
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInElement(mouseX, mouseY)) return;
        int buttonsHeight = buttons.size() * BUTTON_HEIGHT;
        if (buttonsHeight <= height) return;

        int maxScroll = buttons.size() - (height / (BUTTON_HEIGHT + PADDING));

        scrollPosition += (verticalAmount < 0 ? 1 : -1);
        scrollPosition = MathHelper.clamp(scrollPosition, 0, maxScroll);
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    public void clearButtons() {
        buttons.clear();
    }

    public void addButton(String text, int size, Runnable runnable) {
        buttons.add(new ListButton(text, size, runnable));
        int buttonWidth = textRenderer.getWidth(text) + 30;
        if (this.width < buttonWidth) {
            this.width = buttonWidth;
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidestButton() {
        int max = 0;
        for (ListButton button : buttons) {
            int w = textRenderer.getWidth(button.name);
            if (w > max) {
                max = w;
            }
        }
        return max;
    }

    protected class ListButton {
        private final String name;
        private final int size;
        private final Runnable onClick;

        public ListButton(String name, int size, Runnable onClick) {
            this.name = name;
            this.size = size;
            this.onClick = onClick;
        }

        public void render(DrawContext ctx, int mouseX, int mouseY, int x, int y) {
            int color = Color.WHITE.getRGB();
            if (mouseHover(mouseX, mouseY, y)) {
                color = Color.ORANGE.getRGB();
            }

            // Name
            ctx.drawTextWithShadow(textRenderer, name, x + 10, y, color);
            if (size != 0) {
                // Group member count
                ctx.drawTextWithShadow(
                        textRenderer,
                        String.valueOf(size),
                        x + width - textRenderer.getWidth(String.valueOf(size)) - 10,
                        y, Color.GRAY.getRGB()
                );
            }
            // Divider
            ctx.drawHorizontalLine(x + 5, width - 6, y + 12, color);
            ctx.drawHorizontalLine(x + 6, width - 5, y + 13, Color.BLACK.getRGB());
        }

        private boolean mouseHover(double mouseX, double mouseY, int by) {
            return mouseX > x && mouseX < x + width && mouseY > by && mouseY < by + BUTTON_HEIGHT;
        }

        public void mouseClicked(double mouseX, double mouseY, int y) {
            if (mouseHover(mouseX, mouseY, y)) {
                onClick.run();
            }
        }
    }
}
