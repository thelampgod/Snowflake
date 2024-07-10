package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.util.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.elements.ChatElement;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.packets.impl.EncryptedDataPacket;
import com.github.thelampgod.snow.packets.impl.MessagePacket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class ChatWindow extends SnowWindow {

    private final int id;
    private final boolean group;

    protected TextFieldWidget chatInput;
    protected ChatElement chatElement;

    public ChatWindow(int id, boolean group, String title, int width, int height) {
        super(title, false, width, height);
        this.id = id;
        this.group = group;
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        this.chatInput = new TextFieldWidget(this.textRenderer, 0, height - 17, width, 17, Text.empty());
        chatInput.setPlaceholder(Text.of("Type a message..."));
        chatInput.setEditableColor(Color.GRAY.getRGB());
        chatInput.setMaxLength(256);
        this.chatElement = new ChatElement(this.textRenderer, 0, headerHeight, height - headerHeight - chatInput.getHeight(), width);
    }

    @Override
    public void updateDimensions() {
        super.updateDimensions();
        chatInput.setWidth(getWidth());
        chatInput.setY(getHeight() - 17);
        chatElement.setWidth(getWidth());
        chatElement.setHeight(getHeight() - headerHeight - chatInput.getHeight());
    }

    @Override
    public boolean render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        if (!super.render(ctx, mouseX, mouseY, delta)) return false;

        if (!focused && chatInput.isFocused()) chatInput.setFocused(false);
        if (focused && !chatInput.isFocused()) chatInput.setFocused(true);
        chatInput.render(ctx, mouseX, mouseY, delta);
        chatElement.render(ctx, mouseX, mouseY, delta);
        return true;
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        chatInput.charTyped(chr, modifiers);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        chatInput.keyPressed(keyCode, scanCode, modifiers);
        if (chatInput.isFocused() && !chatInput.getText().isEmpty() && (keyCode == 257 || keyCode == 335)) {
            try {
                if (group) {
                    Snow.getServerManager().sendPacket(new EncryptedDataPacket.Group(this.id, new MessagePacket.Group(this.id, chatInput.getText())));
                } else {
                    Snow.getServerManager().sendPacket(new EncryptedDataPacket.User(this.id, new MessagePacket.User(this.id, chatInput.getText())));
                    chatElement.addMessage(Snow.instance.getUserManager().getMeUser().getName(), chatInput.getText());
                }
            } catch (Exception e) {
                Helper.addToast("Failed to send message");
                Snow.instance.getLog().info("Failed to send message");
                e.printStackTrace();
            }
            chatInput.setText("");
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        chatElement.mouseScrolled(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
    }

    public void addMessage(String name, String message) {
        chatElement.addMessage(name, message);
    }

    public void addMessage(String message, Color color) {
        chatElement.addMessage(message, color);
    }

    public int getId() {
        return id;
    }

}