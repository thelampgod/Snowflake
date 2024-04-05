package com.github.thelampgod.snow.mixins.impl;

import net.minecraft.client.font.TextRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextRenderer.class)
public class TextRendererMixin {
    @Redirect(method = "drawWithOutline", at = @At(value = "FIELD", target = "Lnet/minecraft/client/font/TextRenderer$TextLayerType;POLYGON_OFFSET:Lnet/minecraft/client/font/TextRenderer$TextLayerType;"))
    public TextRenderer.TextLayerType fixTransparency() {
        return TextRenderer.TextLayerType.SEE_THROUGH;
    }
}
