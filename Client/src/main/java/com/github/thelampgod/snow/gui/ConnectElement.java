package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.io.IOException;

import static com.github.thelampgod.snow.Helper.mc;
import static com.github.thelampgod.snow.Helper.printModMessage;


public class ConnectElement {

    private int x;
    private int y;
    private final int width = 200;
    private final int height = 60;

    private final int headerHeight = 10;
    //TODO: actually save last ip
    private final static String savedIp = "127.0.0.1:2147";

    private TextFieldWidget inputField;
    private ButtonWidget connectButton;

    public TextRenderer textRenderer;

    public ConnectElement() {
        this.init();
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.resize();
        ctx.fill(0,0,width, height, new Color(139, 139, 139).getRGB());
        ctx.fill(0,0,width,headerHeight, new Color(136, 52, 52).getRGB());
        ctx.drawCenteredTextWithShadow(textRenderer,
                "Connect",
                width / 2,
                headerHeight / 2 - textRenderer.fontHeight / 2,
                Formatting.GOLD.getColorValue());
        this.inputField.render(ctx, mouseX, mouseY, delta);

        boolean hovered = connectButton.isMouseOver(mouseX - x, mouseY - y);
        ctx.drawTextWithShadow(textRenderer,
                connectButton.getMessage(),
                connectButton.getX(),
                (height + headerHeight - textRenderer.fontHeight) / 2,
                hovered ? Color.YELLOW.getRGB() : Color.WHITE.getRGB());
    }

    public void preRender(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MatrixStack stack = ctx.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        render(ctx, mouseX, mouseY, delta);
        stack.pop();
    }


    public void keyPressed(int keyCode, int scanCode, int modifiers) {
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
    }

    public void charTyped(char chr, int modifiers) {
        this.inputField.charTyped(chr, modifiers);
        int messageLength = textRenderer.getWidth(this.inputField.getText());
        this.inputField.setX((this.width - messageLength) / 2);
        this.connectButton.setX((this.width + messageLength + 20) / 2);
    }

    public void resize() {
        String string = this.inputField.getText();
        x = (SnowScreen.scaledWidth - width) / 2;
        y = (SnowScreen.scaledHeight - height) / 2;
        this.inputField.setText(string);
    }

    public final void init() {
        x = (SnowScreen.scaledWidth - width) / 2;
        y = (SnowScreen.scaledHeight - height) / 2;
        textRenderer = mc.textRenderer;

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
    }

}
