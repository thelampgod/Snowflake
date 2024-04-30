package com.github.thelampgod.snow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ExecutionException;

public class Helper {

    public static MinecraftClient mc = MinecraftClient.getInstance();

    public static PrivateKey getPrivateKey() throws ExecutionException, InterruptedException {
        return mc.getProfileKeys().fetchKeyPair().get().get().privateKey();
    }

    public static PublicKey getPublicKey() throws ExecutionException, InterruptedException {
        return mc.getProfileKeys().fetchKeyPair().get().get().publicKey().data().key();
    }

    public static void printModMessage(String msg) {
        MutableText prefix = Text.literal("[Snow] ").formatted(Formatting.LIGHT_PURPLE);
        prefix.append(Text.literal(msg).formatted(Formatting.WHITE));

        printMessage(prefix);
    }

    public static void printModMessage(MutableText msg) {
        MutableText prefix = Text.literal("[Snow] ").formatted(Formatting.LIGHT_PURPLE);
        prefix.append(msg.formatted(Formatting.WHITE));

        printMessage(prefix);
    }
    public static void printMessage(String msg) {
        printMessage(Text.literal(msg));
    }

    public static void printMessage(Text msg) {
        if (mc.inGameHud != null) {
            final ChatHud chat = mc.inGameHud.getChatHud();
            chat.addMessage(msg);
            return;
        }

        Snow.instance.getLog().info(msg.getString());
    }


    public static void addToast(String s) {
        if (mc.player == null) {
            printMessage(s);
            return;
        }
        mc.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.literal(s), null));
    }

    public static void addToast(String title, String description) {
        mc.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.literal(title), Text.literal(description)));
    }
}
