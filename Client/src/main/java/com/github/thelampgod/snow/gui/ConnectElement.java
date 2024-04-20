package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;
import static com.github.thelampgod.snow.Helper.printModMessage;


public class ConnectElement extends SnowWindow {


    //TODO: actually save last ip
    private final static String savedIp = "127.0.0.1:2147";

    private TextFieldWidget inputField;
    private ButtonWidget connectButton;

    public ConnectElement(int width, int height) {
        super("Connect", width, height, false);
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        this.inputField.render(ctx, mouseX, mouseY, delta);

        boolean hovered = connectButton.isMouseOver(mouseX - x, mouseY - y);
        this.drawText(
                connectButton.getMessage().getString(),
                connectButton.getX(),
                (height + headerHeight - textRenderer.fontHeight) / 2,
                hovered ? Color.YELLOW.getRGB() : Color.WHITE.getRGB(), true, ctx);
    }


    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!focused) return;
        this.inputField.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == 259 || Screen.isCut(keyCode)) {
            int messageLength = textRenderer.getWidth(this.inputField.getText());
            this.inputField.setX((this.width - messageLength) / 2);
            this.connectButton.setX((this.width + messageLength + 20) / 2);
        }

        if (this.inputField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.connectButton.onPress();
        }
        super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void charTyped(char chr, int modifiers) {
        if (!focused) return;
        this.inputField.charTyped(chr, modifiers);
        int messageLength = textRenderer.getWidth(this.inputField.getText());
        this.inputField.setX((this.width - messageLength) / 2);
        this.connectButton.setX((this.width + messageLength + 20) / 2);
        super.charTyped(chr, modifiers);
    }

    public void init(int width, int height) {
        super.init(this.width, this.height);

        this.inputField = new TextFieldWidget(textRenderer, (this.width - textRenderer.getWidth("_")) / 2, (height + headerHeight - textRenderer.fontHeight) / 2, this.width - 18, 12, Text.literal("IP"));
        this.inputField.setMaxLength(256);
        this.inputField.setDrawsBackground(false);
        this.inputField.setFocused(true);
        this.inputField.setText(savedIp);

        int buttonWidth = textRenderer.getWidth("Go") + 10;
        this.connectButton = ButtonWidget.builder(Text.literal("Go"),(button -> this.connect()))
                .dimensions(this.inputField.getX() + 20, (height + headerHeight - 12) / 2, buttonWidth, 12)
                .build();
    }

    private void connect() {
        final String ip = this.inputField.getText();
        System.out.println("Connecting to " + ip);
        try {
            Snow.instance.connect(ip);
        } catch (Throwable th) {
            printModMessage("Couldn't connect to " + ip);
            th.printStackTrace();
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        connectButton.mouseClicked(mouseX - x, mouseY - y, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

}
