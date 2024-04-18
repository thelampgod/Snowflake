package com.github.thelampgod.snow.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;

public class SnowWindowElement {
    public int x;
    public int y;
    public int width = 400;
    public int height = 250;
    public int headerHeight = 17;
    private String title;
    private boolean clicked;
    double clickX;
    double clickY;

    public TextRenderer textRenderer;

    public SnowWindowElement(String title, int width, int height) {
        this.title = title;
        this.init(width, height);
    }

    public void init(int width, int height) {
        this.width = width;
        this.height = height;

        x = (SnowScreen.scaledWidth - this.width) / 2 + 20 * SnowScreen.windowList.size();
        y = (SnowScreen.scaledHeight - this.height) / 2 + 20 * SnowScreen.windowList.size();
        textRenderer = mc.textRenderer;
    }


    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, new Color(139, 139, 139).getRGB());
        ctx.fill(0, 0, width, headerHeight, new Color(136, 52, 52).getRGB());
        ctx.drawCenteredTextWithShadow(textRenderer,
                title,
                width / 2,
                headerHeight / 2 - textRenderer.fontHeight / 2,
                Formatting.GOLD.getColorValue());
    }

    public void preRender(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MatrixStack stack = ctx.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        render(ctx, mouseX, mouseY, delta);
        stack.pop();
    }

    public void resize(int width, int height) {
        x = (SnowScreen.scaledWidth - width) / 2;
        y = (SnowScreen.scaledHeight - height) / 2;
    }


    public void keyPressed(int keyCode, int scanCode, int modifiers) {
    }

    public void charTyped(char chr, int modifiers) {
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (cursorInHeader(mouseX, mouseY)) {
            clicked = true;
            clickX = mouseX;
            clickY = mouseY;
        }
    }

    private boolean cursorInHeader(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < headerHeight;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (!clicked) return;
        clicked = false;
        double xDiff = mouseX - clickX;
        double yDiff = mouseY - clickY;

        x += (int) xDiff;
        y += (int) yDiff;
    }
}
