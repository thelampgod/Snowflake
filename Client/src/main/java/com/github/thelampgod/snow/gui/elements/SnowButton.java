package com.github.thelampgod.snow.gui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;

public class SnowButton {

    private String title;
    private final int width;
    private final int height;
    private final int x;
    private final int y;
    private final  Color BUTTON_COLOR = new Color(136, 52, 52);

    private final TextRenderer textRenderer;

    private final Runnable onClick;

    public SnowButton(TextRenderer textRenderer, String title, int x, int y, int width, int height, Runnable onClick) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.textRenderer = textRenderer;
        this.onClick = onClick;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        boolean hovered = cursorInElement(mouseX, mouseY);
        // Header
        ctx.fill(x, y, x + width, y + height, hovered ? BUTTON_COLOR.brighter().getRGB() : BUTTON_COLOR.getRGB());
        // Header Title
        int headerX = x + (width - textRenderer.getWidth(title)) / 2;
        this.drawOutlinedText(
                title,
                headerX,
                y + (height - textRenderer.fontHeight) / 2,
                Formatting.GOLD.getColorValue(),
                0,
                ctx);
    }

    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (cursorInElement(mouseX, mouseY)) {
            onClick.run();
        }
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
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

    public void setTitle(String title) {
        this.title = title;
    }
}
