package com.github.thelampgod.snow.gui.windows;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.util.DrawUtil;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.thelampgod.snow.util.Helper.mc;

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


    public boolean render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (!hasInit) return false;

        // Border
        ctx.fill(-1, -1, (getWidth() + 1), getHeight() + 1, (focused ? Color.WHITE.getRGB() : Color.BLACK.getRGB()));
        // Window
        ctx.fill(0, 0, getWidth(), getHeight(), windowColor.getRGB());
        // Header
        ctx.fill(0, 0, getWidth(), headerHeight, headerColor.getRGB());
        // Header Title
        int headerX = titleCentered ? ((getWidth() - textRenderer.getWidth(title)) / 2) : 5;
        //TODO: fix drawOutlinedText
        DrawUtil.drawText(textRenderer,
                title,
                headerX,
                (headerHeight - textRenderer.fontHeight) / 2,
                Formatting.GOLD.getColorValue(),
                true,
                ctx);
        // Header buttons (close, etc)
        for (TextButton button : headerButtons) {
            button.render(ctx, mouseX, mouseY);
        }
        return true;
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

    public void clear() {

    }

    public TextButton addHeaderButton(String icon, int fontHeight, String tooltip, Runnable runnable) {
        return this.addHeaderButton(icon, fontHeight, tooltip, Color.BLACK, runnable);
    }

    public TextButton addHeaderButton(String icon, int fontHeight, String tooltip, Color color, Runnable runnable) {
        TextButton button = new TextButton(icon,
                getWidth() - (padding + size) * (headerButtons.size() + 1),
                padding, size, color.getRGB(), fontHeight, tooltip, runnable);
        headerButtons.add(button);
        return button;
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
        private int color;
        private final int fontHeight;
        private String tooltipText;
        private final Runnable runnable;
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
            DrawUtil.drawText(textRenderer, icon, (size - textRenderer.getWidth(icon)) / 2 + 1, (size - fontHeight) / 2, color, false, ctx);
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

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public void setTooltip(String tooltipText) {
            this.tooltipText = tooltipText;
        }
    }
}
