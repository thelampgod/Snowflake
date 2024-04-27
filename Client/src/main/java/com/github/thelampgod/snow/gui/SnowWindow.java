package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
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

    public Color windowColor = new Color(139, 139, 139);
    public Color headerColor = new Color(136, 52, 52);
    private final String title;
    private final boolean titleCentered;
    private boolean clicked;
    protected boolean focused;
    public boolean hasInit = false;

    public boolean closeable;
    private TextButton close;

    protected int size = 9;
    protected int padding = (headerHeight - size) / 2;
    public TextRenderer textRenderer;

    public SnowWindow(String title, boolean titleCentered, int width, int height, boolean closeable) {
        this.title = title;
        this.titleCentered = titleCentered;
        this.width = width;
        this.height = height;
        this.closeable = closeable;
        x = (double) (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (double) (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        this.textRenderer = mc.textRenderer;
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

        x = (double) (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (double) (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        textRenderer = mc.textRenderer;
        close = new TextButton("x", width - padding - size, padding, size, Color.BLACK.getRGB());
        this.hasInit = true;
    }


    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Border
        ctx.fill(-1, -1, width + 1, height + 1, (focused ? Color.WHITE.getRGB() : Color.BLACK.getRGB()));
        // Window
        ctx.fill(0, 0, width, height, windowColor.getRGB());
        // Header
        ctx.fill(0, 0, width, headerHeight, headerColor.getRGB());
        // Header Title
        int headerX = titleCentered ? (width - textRenderer.getWidth(title)) / 2 : 5;
        this.drawOutlinedText(
                title,
                headerX,
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


    protected class TextButton {
        String icon;
        int bx;
        int by;
        int size;
        int color;
        int fontHeight;
        String tooltipText;

        //TODO: general button class, with icon and runnable?
        public TextButton(String iconChar, int x, int y, int size, int color, int fontHeight, String tooltipText) {
            this.icon = iconChar;
            this.bx = x;
            this.by = y;
            this.size = size;
            this.color = color;
            this.fontHeight = fontHeight;
            this.tooltipText = tooltipText;
        }

        public TextButton(String iconChar, int x, int y, int size, int color) {
            this(iconChar, x,y,size,color, textRenderer.fontHeight, "");
        }

        public void render(DrawContext ctx, int mouseX, int mouseY) {
            ctx.getMatrices().push();
            ctx.getMatrices().translate(bx,by,0);
            if (mouseHover(mouseX, mouseY)) {
                ctx.fill(0, 0, size, size, headerColor.brighter().getRGB());
            }
            ctx.drawText(textRenderer, icon, (size - textRenderer.getWidth(icon)) / 2 + 1, (size - fontHeight) / 2 , color, false);
            if (!tooltipText.isEmpty() && mouseHover(mouseX, mouseY)) {
                ctx.drawTooltip(textRenderer, Text.literal(tooltipText), 0, 0);
            }
            ctx.getMatrices().pop();
        }

        public boolean mouseHover(double mouseX, double mouseY) {
            return mouseX - x - bx > 0 & mouseX - x - bx < size && mouseY - y - by > 0 && mouseY - y - by < size;
        }
    }
}
