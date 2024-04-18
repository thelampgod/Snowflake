package com.github.thelampgod.snow.mixins.impl;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public class DrawContextMixin {

    @Redirect(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"
            ))
    public int fixLayerType(TextRenderer instance, OrderedText text, float x, float y, int color,
                            boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers,
                            TextRenderer.TextLayerType layerType, int backgroundColor, int light) {
        return instance.draw(text, x, y, color, shadow, matrix, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, light);
    }

    @Redirect(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I"
            ))
    public int fixLayerType2(TextRenderer instance, String text, float x, float y, int color,
                             boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers,
                             TextRenderer.TextLayerType layerType, int backgroundColor, int light, boolean rightToLeft) {
        return instance.draw(text, x, y, color, shadow, matrix, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, backgroundColor, light, rightToLeft);
    }
}
