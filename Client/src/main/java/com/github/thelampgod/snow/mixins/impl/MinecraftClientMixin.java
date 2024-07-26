package com.github.thelampgod.snow.mixins.impl;

import com.github.thelampgod.snow.Snow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method="tick", at = @At(value = "HEAD"))
    public void run(CallbackInfo ci) {
        Snow.instance.getRenderer().tick();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "RETURN"))
    public void onDisconnect(Screen disconnectionScreen, CallbackInfo ci) {
        Snow.instance.getSharer().onWorldUnload();
    }
}
