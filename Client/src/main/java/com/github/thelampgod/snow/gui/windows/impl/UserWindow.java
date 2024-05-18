package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.elements.ChatElement;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.packets.impl.EncryptedDataPacket;
import com.github.thelampgod.snow.packets.impl.MessagePacket;
import com.github.thelampgod.snow.users.User;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class UserWindow extends SnowWindow {
    private final int id;
    private TextFieldWidget chatInput;
    private ChatElement chatElement;

    public UserWindow(User user) {
        super(user.getName(), false, 200, 100);
        this.id = user.getId();
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
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);

        if (!focused && chatInput.isFocused()) chatInput.setFocused(false);
        if (focused && !chatInput.isFocused()) chatInput.setFocused(true);
        chatInput.render(ctx, mouseX, mouseY, delta);
        chatElement.render(ctx, mouseX, mouseY, delta);
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
        if (chatInput.isFocused() && ! chatInput.getText().isEmpty() && (keyCode == 257 || keyCode == 335)) {
            try {
                Snow.getServerManager().sendPacket(new EncryptedDataPacket.User(this.id, new MessagePacket.User(this.id, chatInput.getText())));
            } catch (Exception e) {
                Snow.instance.getLog().info("Failed to send message");
                e.printStackTrace();
            }
            chatElement.addMessage("You", chatInput.getText());
            chatInput.setText("");
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        chatElement.mouseScrolled(mouseX - x, mouseY - y, horizontalAmount, verticalAmount);
    }

    public int getId() {
        return id;
    }
}
