package com.github.thelampgod.snow.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;


public class ConnectElement {

    int x;
    int y;
    int width = 200;
    int height = 80;

    private TextFieldWidget inputField;

    public ConnectElement() {
        this.init();
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.resize();
        ctx.fill(0,0,width, height, new Color(139, 139, 139, 143).getRGB());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, Text.literal("Connect"), width / 2, 20, Color.WHITE.getRGB());
        this.inputField.render(ctx, mouseX, mouseY, delta);
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
        }
    }

    public void charTyped(char chr, int modifiers) {
        this.inputField.charTyped(chr, modifiers);
        int messageLength = mc.textRenderer.getWidth(this.inputField.getText());
        this.inputField.setX((this.width - messageLength) / 2);
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

        this.inputField = new TextFieldWidget(mc.textRenderer, (this.width - mc.textRenderer.getWidth("_")) / 2, 40, this.width - 18, 12, Text.literal("IP"));
        this.inputField.setMaxLength(256);
        this.inputField.setDrawsBackground(false);
        this.inputField.setFocused(true);
        this.inputField.setText("");
    }
}
