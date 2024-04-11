package com.github.thelampgod.snow.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SnowScreen extends Screen {

    private ConnectElement connectElement;
    public static int scaledWidth;
    public static int scaledHeight;
    public SnowScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        connectElement = new ConnectElement();
    }
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        scaledWidth = ctx.getScaledWindowWidth();
        scaledHeight = ctx.getScaledWindowHeight();
        connectElement.preRender(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        connectElement.keyPressed(keyCode, scanCode, modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        connectElement.charTyped(chr, modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        connectElement.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        connectElement.isMouseOver(mouseX, mouseY);
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        connectElement.resize();
        super.resize(client, width, height);
    }
}
