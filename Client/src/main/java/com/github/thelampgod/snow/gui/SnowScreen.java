package com.github.thelampgod.snow.gui;

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
        super.init();
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
}
