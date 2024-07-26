package com.github.thelampgod.snow.mixins.impl;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {

    @Final
    @Accessor
    Map<UUID, PlayerListEntry> getPlayerListEntries();
}
