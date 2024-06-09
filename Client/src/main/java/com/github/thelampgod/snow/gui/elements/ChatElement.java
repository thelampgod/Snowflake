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
    private int height;
    private int width;
    private static final int PADDING = 3;
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
            boolean sameSender = prevSender == null || prevSender.equals(message.sender) || message.sender == null;

            if (prevSender == null && message.sender != null) {
                sameSender = false;
            }
            prevSender = message.sender;

            // Draw sender name
            if (!sameSender && textY > y && textY < y + height) {
                ctx.drawText(this.textRenderer, message.sender, x + PADDING, textY, Color.YELLOW.getRGB(), false);
                textY += textRenderer.fontHeight;
            }

            // Draw message
            if (textY > y && textY < y + height) {
                if (textRenderer.getWidth(message.message) > width - PADDING * 2) {
                    List<OrderedText> wrapped = textRenderer.wrapLines(Text.of(message.message), width - PADDING * 2);
                    for (OrderedText text : wrapped) {
                        if (textY > y && textY < y + height) {
                            ctx.drawText(this.textRenderer, text, x + PADDING, textY, message.color, false);
                            textY += textRenderer.fontHeight;
                        }
                    }
                } else {
                    ctx.drawText(this.textRenderer, message.message, x + PADDING, textY, message.color, false);
                    textY += textRenderer.fontHeight;
                }
            }
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInElement(mouseX, mouseY)) return;
        int messageHeight = getHeight(messages);
        if (messageHeight < height) return;

        int diff = (messageHeight - height) / textRenderer.fontHeight + 3;

        scrollPosition += (verticalAmount < 0 ? 1 : -1);
        scrollPosition = MathHelper.clamp(scrollPosition, 0, diff);
    }

    private int getHeight(List<ChatMessage> messages) {
        int height = 0;
        String prevSender = "";
        for (ChatMessage message : messages) {
            boolean sameSender = prevSender == null || prevSender.equals(message.sender) || message.sender == null;
            if (prevSender == null && message.sender != null) {
                sameSender = false;
            }

            height += (sameSender ? textRenderer.fontHeight : textRenderer.fontHeight * 2);
            prevSender = message.sender;

            if (textRenderer.getWidth(message.message) > width - PADDING * 2) {
                List<OrderedText> wrapped = textRenderer.wrapLines(Text.of(message.message), width - PADDING * 2);
                height += wrapped.size();
            }
        }
        return height;
    }

    private boolean cursorInElement(double mouseX, double mouseY) {
        return mouseX - x > 0 && mouseX - x < width && mouseY - y > 0 && mouseY - y < height;
    }

    public void addMessage(String message, Color color) {
        this.addMessage(null, message, color);
    }

    public void addMessage(String sender, String message) {
        this.addMessage(sender, message, Color.BLACK);
    }

    public void addMessage(String sender, String message, Color color) {
        messages.add(new ChatMessage(sender, message, color.getRGB(), System.currentTimeMillis()));

        // Scroll down to latest message
        int messageHeight = getHeight(messages);
        if (messageHeight < height) return;

        scrollPosition = (messageHeight - height) / textRenderer.fontHeight + 3;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected record ChatMessage(String sender, String message, int color, long timestamp) {
    }
}
