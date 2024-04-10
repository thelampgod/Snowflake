package com.github.thelampgod.snow.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;


public class ConnectElement {

    int x;
    int y;
    int width = 200;
    int height = 80;

    public ConnectElement() {
        x = (SnowScreen.scaledWidth - width) / 2;
        y = (SnowScreen.scaledHeight - height) / 2;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0,0,width, height, new Color(139, 139, 139, 143).getRGB());
        ctx.drawCenteredTextWithShadow(mc.textRenderer, Text.literal("Connect"), width / 2, 20, Color.WHITE.getRGB());
    }

    public void preRender(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MatrixStack stack = ctx.getMatrices();
        stack.push();
        stack.translate(x, y, 0);
        render(ctx, mouseX, mouseY, delta);
        stack.pop();
    }
}
