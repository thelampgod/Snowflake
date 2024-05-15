package com.github.thelampgod.snow.gui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
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

    int maxButtonHeight = 0;
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
        String prevSender = "";
        int textY = y + PADDING;
        for (int i = scrollPosition; i < messages.size(); ++i) {
            ChatMessage message = messages.get(i);
            boolean sameSender = prevSender.equals(message.sender);
            prevSender = message.sender;

            if (!sameSender && textY > y && textY < y + height) {
                ctx.drawText(this.textRenderer, message.sender, x + PADDING, textY, Color.YELLOW.getRGB(), false);
                textY += textRenderer.fontHeight;
            }

            if (textY > y && textY < y + height) {
                if (textRenderer.getWidth(message.message) > width - PADDING * 2) {
                    List<OrderedText> wrapped = textRenderer.wrapLines(Text.of(message.message), width - PADDING * 2);
                    for (OrderedText text : wrapped) {
                        if (textY > y && textY < y + height) {
                            ctx.drawText(this.textRenderer, text, x + PADDING, textY, Color.BLACK.getRGB(), false);
                            textY += textRenderer.fontHeight;
                        }
                    }
                } else {
                    ctx.drawText(this.textRenderer, message.message, x + PADDING, textY, Color.BLACK.getRGB(), false);
                    textY += textRenderer.fontHeight;
                }
            }
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInElement(mouseX, mouseY)) return;

        scrollPosition += (verticalAmount < 0 ? 1 : -1);
        scrollPosition = MathHelper.clamp(scrollPosition, 0, Math.max(messages.size() - 1, 0));
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    public void addMessage(String sender, String message) {
        messages.add(new ChatMessage(sender, message, System.currentTimeMillis()));

        // Scroll down to latest message
        scrollPosition = Math.max(messages.size() - 1, 0);
    }

    protected record ChatMessage(String sender, String message, long timestamp) {
    }
}
