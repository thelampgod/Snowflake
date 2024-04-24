package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;

public class SnowWindow {
    public double x;
    public double y;
    public int width;
    public int height;
    public int headerHeight = 17;
    private String title;
    private boolean clicked;
    protected boolean focused;
    public boolean hasInit = false;

    public boolean closeable;
    private CloseButton close;

    public TextRenderer textRenderer;

    public SnowWindow(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.closeable = true;
        x = (double) (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (double) (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        textRenderer = mc.textRenderer;
    }

    public SnowWindow(String title, int width, int height, boolean closeable) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.closeable = closeable;
        x = (double) (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (double) (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        textRenderer = mc.textRenderer;
    }

    public void init(int width, int height) {
        if (hasInit) return;
        this.width = width;
        this.height = height;

        x = (double) (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (double) (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        textRenderer = mc.textRenderer;

        int padding = 5;
        int size = 7;
        close = new CloseButton(width - padding - size, padding, size, size, Color.BLACK.getRGB());
        this.hasInit = true;
    }


    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Window
        ctx.fill(0, 0, width, height, new Color(139, 139, 139, (focused ? 255 : 180)).getRGB());
        // Header
        ctx.fill(0, 0, width, headerHeight, new Color(136, 52, 52, (focused ? 255 : 180)).getRGB());
        // Header Title
        this.drawOutlinedText(
                title,
                (width - textRenderer.getWidth(title)) / 2,
                (headerHeight - textRenderer.fontHeight) / 2,
                Formatting.GOLD.getColorValue(),
                0,
                ctx);

        if (closeable) {
            close.render(ctx, mouseX, mouseY);
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

        if (closeable && close.mouseHover(mouseX, mouseY)) {
            Snow.instance.getOrCreateSnowScreen().remove(this);
        }
    }

    public boolean cursorInWindow(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    private boolean cursorInHeader(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < headerHeight;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        clicked = false;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!clicked) return;

        x += deltaX;
        y += deltaY;
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

    private class CloseButton {
        int bx;
        int by;
        int w;
        int h;
        int color;

        //TODO: general button class, with icon and runnable?
        public CloseButton(int x, int y, int w, int h, int color) {
            this.bx = x;
            this.by = y;
            this.w = w;
            this.h = h;
            this.color = color;
        }

        public void render(DrawContext ctx, int mouseX, int mouseY) {
            if (!mouseHover(mouseX, mouseY)) {
                ctx.fill(bx + 1, by + 1, bx + 1 + w, by + 1 + h, color);
                ctx.fill(bx, by, bx + w, by + h, Color.GRAY.getRGB());
                return;
            }

            ctx.fill(bx-1, by-1, bx-1 + w, by-1 + h, color);
            ctx.fill(bx, by, bx + w, by + h, Color.GRAY.getRGB());
        }

        private boolean mouseHover(double mouseX, double mouseY) {
            return mouseX - x - bx > 0 & mouseX - x - bx < w && mouseY - y - by > 0 && mouseY - y - by < h;
        }
    }
}
