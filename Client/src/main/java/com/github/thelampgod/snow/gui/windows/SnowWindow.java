package com.github.thelampgod.snow.gui.windows;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.thelampgod.snow.Helper.mc;

public abstract class SnowWindow {
    public double x;
    public double y;
    public double width;
    public double height;
    public int headerHeight = 17;

    public Color windowColor = new Color(139, 139, 139);
    public Color headerColor = new Color(136, 52, 52);
    private final String title;
    private final boolean titleCentered;
    private boolean clicked;
    private boolean resizing;
    public boolean focused;
    public boolean hasInit = false;

    protected final List<TextButton> headerButtons = new ArrayList<>();
    public boolean closeable;

    protected int size = 9;
    protected int padding = (headerHeight - size) / 2;
    public TextRenderer textRenderer;

    public SnowWindow(String title, boolean titleCentered, int width, int height, boolean closeable) {
        this.title = title;
        this.titleCentered = titleCentered;
        this.width = width;
        this.height = height;
        this.closeable = closeable;
        x = (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        this.textRenderer = mc.textRenderer;
        if (textRenderer.getWidth(title) > width) {
            this.width = textRenderer.getWidth(title) + 40;
        }
    }

    public SnowWindow(String title, boolean titleCentered, int width, int height) {
        this(title, titleCentered, width, height, true);
    }

    public SnowWindow(String title, int width, int height, boolean closeable) {
        this(title, true, width, height, closeable);
    }

    public void init(int width, int height) {
        if (hasInit) return;
        this.width = width;
        this.height = height;

        x = (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        textRenderer = mc.textRenderer;
        if (closeable) {
            addHeaderButton("x", textRenderer.fontHeight, "", () -> Snow.instance.getOrCreateSnowScreen().remove(this));
        }
        this.hasInit = true;
    }


    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Border
        ctx.fill(-1, -1, (getWidth() + 1), getHeight() + 1, (focused ? Color.WHITE.getRGB() : Color.BLACK.getRGB()));
        // Window
        ctx.fill(0, 0, getWidth(), getHeight(), windowColor.getRGB());
        // Header
        ctx.fill(0, 0, getWidth(), headerHeight, headerColor.getRGB());
        // Header Title
        int headerX = titleCentered ? ((getWidth() - textRenderer.getWidth(title)) / 2) : 5;
        this.drawOutlinedText(
                title,
                headerX,
                (headerHeight - textRenderer.fontHeight) / 2,
                Formatting.GOLD.getColorValue(),
                0,
                ctx);
        // Header buttons (close, etc)
        for (TextButton button : headerButtons) {
            button.render(ctx, mouseX, mouseY);
        }
    }


    public void preRender(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MatrixStack stack = ctx.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        render(ctx, mouseX, mouseY, delta);
        stack.pop();
    }


    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char chr, int modifiers) {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (cursorInWindow(mouseX, mouseY)) {
            Snow.instance.getOrCreateSnowScreen().focusWindow(this);
        } else {
            focused = false;
        }

        if (cursorInHeader(mouseX, mouseY)) {
            clicked = true;
        }

        if (cursorInBottomCorner(mouseX, mouseY)) {
            resizing = true;
        }

        for (TextButton b : headerButtons) {
            b.mouseClicked(mouseX, mouseY, button);
        }
    }

    private boolean cursorInBottomCorner(double mouseX, double mouseY) {
        return mouseX - x > width - 5 && mouseX - x < width && mouseY - y > height - 5 && mouseY - y < height;
    }

    public boolean cursorInWindow(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    private boolean cursorInHeader(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < headerHeight;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        clicked = false;
        resizing = false;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (clicked) {
            x += deltaX;
            y += deltaY;
        }

        if (resizing) {
            width += deltaX;
            height += deltaY;

            width = Math.max(this.width, this.textRenderer.getWidth(this.title) + (padding + size) * (headerButtons.size() + 1));
            height = Math.max(this.height, this.headerHeight);
            updateDimensions();
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    }

    public void updateDimensions() {
        for (int i = 0; i < headerButtons.size(); ++i) {
            TextButton button = headerButtons.get(i);
            button.setX(getWidth() - (padding + size) * (i + 1));
        }
    }

    public void addHeaderButton(String icon, int fontHeight, String tooltip, Runnable runnable) {
        headerButtons.add(new TextButton(icon,
                getWidth() - (padding + size) * (headerButtons.size() + 1),
                padding, size, Color.BLACK.getRGB(), fontHeight, tooltip, runnable));
    }

    public void drawOutlinedText(String title, int x, int y, int color, int bgColor, DrawContext ctx) {
        textRenderer.drawWithOutline(
                Text.literal(title).asOrderedText(),
                (float) x,
                (float) y,
                color,
                bgColor,
                ctx.getMatrices().peek().getPositionMatrix(),
                ctx.getVertexConsumers(), 255);
    }

    public void drawText(String title, int x, int y, int color, boolean shadow, DrawContext ctx) {
        textRenderer.draw(
                Text.literal(title).asOrderedText(),
                (float) x,
                (float) y,
                color,
                shadow,
                ctx.getMatrices().peek().getPositionMatrix(),
                ctx.getVertexConsumers(), TextRenderer.TextLayerType.SEE_THROUGH, 0, 255);
    }

    public int getWidth() {
        return (int) width;
    }

    public int getHeight() {
        return (int) height;
    }


    protected class TextButton {
        private final String icon;
        private int bx;
        private final int by;
        private final int size;
        private final int color;
        private final int fontHeight;
        private final String tooltipText;
        private final Runnable runnable;

        //TODO: general button class, with icon and runnable?
        public TextButton(String iconChar, int x, int y, int size, int color, int fontHeight, String tooltipText, Runnable runnable) {
            this.icon = iconChar;
            this.bx = x;
            this.by = y;
            this.size = size;
            this.color = color;
            this.fontHeight = fontHeight;
            this.tooltipText = tooltipText;
            this.runnable = runnable;
        }

        public void render(DrawContext ctx, int mouseX, int mouseY) {
            ctx.getMatrices().push();
            ctx.getMatrices().translate(bx, by, 0);
            if (mouseHover(mouseX, mouseY)) {
                ctx.fill(0, 0, size, size, headerColor.brighter().getRGB());
            }
            ctx.drawText(textRenderer, icon, (size - textRenderer.getWidth(icon)) / 2 + 1, (size - fontHeight) / 2, color, false);
            if (!tooltipText.isEmpty() && mouseHover(mouseX, mouseY)) {
                ctx.drawTooltip(textRenderer, Text.literal(tooltipText), 0, 0);
            }
            ctx.getMatrices().pop();
        }

        public boolean mouseHover(double mouseX, double mouseY) {
            return mouseX - x - bx > 0 & mouseX - x - bx < size && mouseY - y - by > 0 && mouseY - y - by < size;
        }

        public void mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseHover(mouseX, mouseY)) {
                runnable.run();
            }
        }


        public void setX(int x) {
            bx = x;
        }
    }
}
