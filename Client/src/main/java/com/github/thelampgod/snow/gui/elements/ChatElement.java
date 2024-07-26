package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.util.DrawUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public ChatElement(TextRenderer textRenderer, int x, int y, int height, int width) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        String prevSender = "";
        DateFormat timeFormat = new SimpleDateFormat(" <HH:mm>");
        int textY = y + PADDING;
        for (int i = scrollPosition; i < messages.size(); ++i) {
            ChatMessage message = messages.get(i);
            Date date = new Date(message.timestamp);
            String timestamp = Formatting.GRAY + timeFormat.format(date) + Formatting.RESET;

            String sender = message.sender + timestamp;
            boolean sameSender = prevSender.equals(sender);
            prevSender = sender;

            // Draw sender name
            if (!sameSender && inBounds(textY)) {
                DrawUtil.drawText(this.textRenderer, sender, x + PADDING, textY, Color.YELLOW.getRGB(), false, ctx);
                textY += textRenderer.fontHeight;
            }

            // Draw message
            if (inBounds(textY)) {
                if (textRenderer.getWidth(message.message) > width - PADDING * 2) {
                    List<OrderedText> wrapped = textRenderer.wrapLines(Text.of(message.message), width - PADDING * 2);
                    for (OrderedText text : wrapped) {
                        if (inBounds(textY)) {
                            DrawUtil.drawText(this.textRenderer, text, x + PADDING, textY, message.color, false, ctx);
                            textY += textRenderer.fontHeight;
                        }
                    }
                } else {
                    DrawUtil.drawText(this.textRenderer, message.message, x + PADDING, textY, message.color, false, ctx);
                    textY += textRenderer.fontHeight;
                }
            }
        }
    }

    private boolean inBounds(int textY) {
        return textY > y && textY < y + height - textRenderer.fontHeight;
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!cursorInElement(mouseX, mouseY)) return;
        int messageHeight = getHeight(messages);
        if (messageHeight < height) {
            scrollPosition = 0;
            return;
        }

        // how many lines that are not rendered currently
        int diff = (messageHeight - height) / textRenderer.fontHeight + 1;

        scrollPosition += (verticalAmount < 0 ? 1 : -1);
        scrollPosition = MathHelper.clamp(scrollPosition, 0, Math.min(messages.size() - 1, diff));
    }

    private int getHeight(List<ChatMessage> messages) {
        int height = PADDING;
        String prevSender = "";
        DateFormat timeFormat = new SimpleDateFormat("<HH:mm>");
        for (ChatMessage message : messages) {
            Date date = new Date(message.timestamp);
            String timestamp = Formatting.GRAY + timeFormat.format(date) + Formatting.RESET + " ";
            String sender = message.sender + timestamp;
            boolean sameSender = prevSender.equals(sender);

            height += (sameSender ? textRenderer.fontHeight : textRenderer.fontHeight * 2);
            prevSender = sender;

            if (textRenderer.getWidth(message.message) > width - PADDING * 2) {
                List<OrderedText> wrapped = textRenderer.wrapLines(Text.of(message.message), width - PADDING * 2);
                height += (wrapped.size() - 1) * textRenderer.fontHeight;
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
        if (messageHeight < height) {
            scrollPosition = 0;
            return;
        }

        // how many lines that are not rendered currently
        int diff = (messageHeight - height) / textRenderer.fontHeight + 1;
        scrollPosition = diff;
        scrollPosition = MathHelper.clamp(scrollPosition, 0, Math.min(messages.size() - 1, diff));
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
