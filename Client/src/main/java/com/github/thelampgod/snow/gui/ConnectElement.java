package com.github.thelampgod.snow.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;


public class ConnectElement {

    int x;
    int y;
    int width = 200;
    int height = 60;

    int headerHeight = 10;

    private TextFieldWidget inputField;
    private ButtonWidget connectButton;

    public ConnectElement() {
        this.init();
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.resize();
        ctx.fill(0,0,width, height, new Color(139, 139, 139).getRGB());
        ctx.fill(0,0,width,headerHeight, new Color(136, 52, 52).getRGB());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, "Connect", width / 2, headerHeight / 2 - mc.textRenderer.fontHeight / 2, Formatting.GOLD.getColorValue());
        this.inputField.render(ctx, mouseX, mouseY, delta);
        this.connectButton.render(ctx, mouseX, mouseY, delta);
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
            int messageLength = mc.textRenderer.getWidth(this.inputField.getText());
            this.inputField.setX((this.width - messageLength) / 2);
            this.connectButton.setX((this.width + messageLength + 20) / 2);
        }

        if (this.inputField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            this.connectButton.onPress();
        }
    }

    public void charTyped(char chr, int modifiers) {
        this.inputField.charTyped(chr, modifiers);
        int messageLength = mc.textRenderer.getWidth(this.inputField.getText());
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

        this.inputField = new TextFieldWidget(mc.textRenderer, (this.width - mc.textRenderer.getWidth("_")) / 2, (height + headerHeight - mc.textRenderer.fontHeight) / 2, this.width - 18, 12, Text.literal("IP"));
        this.inputField.setMaxLength(256);
        this.inputField.setDrawsBackground(false);
        this.inputField.setFocused(true);
        this.inputField.setText("");

        int buttonWidth = mc.textRenderer.getWidth("Go") + 10;
        this.connectButton = ButtonWidget.builder(Text.literal("Go"),(button -> this.connect()))
                .dimensions(this.inputField.getX() + 20, (height + headerHeight - 12) / 2, buttonWidth, 12)
                .build();
    }

    private void connect() {
        System.out.println("Connecting to " + this.inputField.getText());
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        connectButton.mouseClicked(mouseX - x, mouseY - y, button);
    }

    //todo doesnt work?
    public void isMouseOver(double mouseX, double mouseY) {
        if (connectButton.isMouseOver(mouseX - x, mouseY - y)) {
            System.out.println("yepppers!!!!");
        }
    }

}
