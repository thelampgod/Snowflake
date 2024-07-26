package com.github.thelampgod.snow.mixins.impl;

import com.github.thelampgod.snow.Snow;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    public void bob(GameRenderer instance, MatrixStack matrices, float tickDelta) {
        if (!Snow.getServerManager().isConnected()) {
            this.bobView(matrices, tickDelta);
        }
    }
}
