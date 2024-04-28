package com.github.thelampgod.snow.commands.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.packets.impl.EncryptedDataPacket;
import com.github.thelampgod.snow.packets.impl.MessagePacket;
import com.github.thelampgod.snow.packets.impl.outgoing.ListUsersPacket;
import com.github.thelampgod.snow.packets.impl.outgoing.LoginStartPacket;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.security.PublicKey;

import static com.github.thelampgod.snow.Helper.mc;
import static com.github.thelampgod.snow.Helper.printModMessage;

public class AuthenticateCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return ClientCommandManager.literal("snow")
                .then(ClientCommandManager.literal("authenticate")
                        .executes(AuthenticateCommand::authenticate))
                .then(ClientCommandManager.literal("list")
                        .executes(AuthenticateCommand::list))
                .then(ClientCommandManager.literal("send")
                        .then(ClientCommandManager.argument("group", IntegerArgumentType.integer())
                                .then(ClientCommandManager.argument("message", StringArgumentType.string())
                                        .executes(AuthenticateCommand::sendMessage))))
                .then(ClientCommandManager.literal("test")
                        .then(ClientCommandManager.argument("message", StringArgumentType.string())
                        .executes(AuthenticateCommand::test)));
    }

    private static int test(CommandContext<FabricClientCommandSource> ctx) {
        try {
            final String message = ctx.getArgument("message", String.class);
            byte[] enc = EncryptionUtil.encrypt(message.getBytes(), Helper.getPublicKey());
            System.out.println(new String(enc));
            byte[] dec = EncryptionUtil.decrypt(enc, Helper.getPrivateKey());
            System.out.println(new String(dec));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static int sendMessage(CommandContext<FabricClientCommandSource> ctx) {
        try {
            final int groupId = ctx.getArgument("group", Integer.class);
            final String message = ctx.getArgument("message", String.class);
            Snow.getServerManager().sendPacket(new EncryptedDataPacket.Group(groupId, new MessagePacket.Group(groupId, message)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static int list(CommandContext<FabricClientCommandSource> ctx) {
        Snow.getServerManager().sendPacket(new ListUsersPacket());
        return 1;
    }

    private static int authenticate(CommandContext<FabricClientCommandSource> ctx) {
        try {
            PublicKey pub = mc.getProfileKeys().fetchKeyPair().get().get().publicKey().data().key();
            Snow.getServerManager().sendPacket(new LoginStartPacket(
                    EncryptionUtil.asciiArmored(pub)));
        } catch (Exception e) {
            printModMessage("Couldn't get public key.");
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}
