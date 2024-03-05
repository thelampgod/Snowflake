package com.github.thelampgod.snow.commands.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.impl.LoginStartPacket;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.security.PublicKey;

import static com.github.thelampgod.snow.Helper.mc;
import static com.github.thelampgod.snow.Helper.printModMessage;

public class AuthenticateCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("snow").then(
                ClientCommandManager.literal("authenticate").executes(AuthenticateCommand::authenticate));
    }

    private static int authenticate(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        try {
            PublicKey pub = mc.getProfileKeys().fetchKeyPair().get().get().publicKey().data().key();
            System.out.println(pub.getClass().getSimpleName());
            Snow.getServerManager().sendPacket(new LoginStartPacket(
                    pub.getEncoded()));
//
//            System.out.println(pub);
        } catch (Exception e) {
            printModMessage("Couldn't get public key.");
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
