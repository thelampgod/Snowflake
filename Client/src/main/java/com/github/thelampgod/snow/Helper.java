package com.github.thelampgod.snow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class Helper {

    public static MinecraftClient mc = MinecraftClient.getInstance();

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

}
