package com.github.thelampgod.snow.mixins.impl;

import com.github.thelampgod.snow.Snow;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", at = @At(value = "HEAD"))
    public void onPositionSending(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround
        || packet instanceof PlayerMoveC2SPacket.Full) {
            PlayerMoveC2SPacket p = (PlayerMoveC2SPacket) packet;
            Snow.instance.getSharer().onLocationSend(p);
        }
    }
}
