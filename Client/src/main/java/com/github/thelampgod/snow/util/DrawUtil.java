package com.github.thelampgod.snow.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class DrawUtil {
    public static double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }

    public static void drawText(TextRenderer textRenderer, String text, int x, int y, int color, boolean shadow, DrawContext ctx) {
        DrawUtil.drawText(textRenderer, Text.literal(text).asOrderedText(), x, y, color, shadow, ctx);
    }

    public static void drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow, DrawContext ctx) {
        textRenderer.draw(
                text,
                (float) x,
                (float) y,
                color,
                shadow,
                ctx.getMatrices().peek().getPositionMatrix(),
                ctx.getVertexConsumers(), TextRenderer.TextLayerType.SEE_THROUGH, 0, 255);
    }

//    public static void fill(int x1, int x2, int y1, int y2, int color) {
//        BufferBuilder builder = new BufferBuilder(0);
//
//        builder.
//    }
}
