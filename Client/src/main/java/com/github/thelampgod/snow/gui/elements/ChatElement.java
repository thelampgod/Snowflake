package com.github.thelampgod.snow.gui.elements;


import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatElement {

    private final int x;
    private final int y;
    private final int height;
    private int width;
    private static final int PADDING = 3;

    private static final int MESSAGE_HEIGHT = 18;
    private final List<ChatMessage> messages = new ArrayList<>();

    private final TextRenderer textRenderer;

    private int scrollPosition = 0;
    private static final int SCROLL_SPEED = 9;

    public ChatElement(TextRenderer textRenderer, int x, int y, int height, int width) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        for (int i = 0; i < messages.size(); ++i) {
            ChatMessage message = messages.get(i);

            int textY = y + i * MESSAGE_HEIGHT + scrollPosition * SCROLL_SPEED + PADDING;
            if (textY > y && textY < y + height - textRenderer.fontHeight) {
                ctx.drawText(this.textRenderer, message.sender, x + PADDING, textY, Color.YELLOW.getRGB(), false);
            }

            if (textY + textRenderer.fontHeight > y && textY + textRenderer.fontHeight < y+height - textRenderer.fontHeight) {
                ctx.drawText(this.textRenderer, message.message, x + PADDING, textY + textRenderer.fontHeight, Color.BLACK.getRGB(), false);
            }
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInElement(mouseX, mouseY)) return;

        scrollPosition += (verticalAmount > 0 ? 1 : -1);
        scrollPosition = MathHelper.clamp(scrollPosition, -minScroll(), 0);
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    public void addMessage(String sender, String message) {
        messages.add(new ChatMessage(sender, message, System.currentTimeMillis()));

        // Scroll down to latest message
        scrollPosition = -minScroll();
    }

    private int minScroll() {
        int buttonsHeight = (messages.size()) * MESSAGE_HEIGHT;
        if (buttonsHeight < height) return 0;

        return (buttonsHeight - (height)) / SCROLL_SPEED + 2;
    }

    protected record ChatMessage(String sender, String message, long timestamp){}
}
