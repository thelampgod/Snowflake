package com.github.thelampgod.snow.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SnowScreen extends Screen {

    private ConnectElement connectElement;
    public static int scaledWidth;
    public static int scaledHeight;

    public static List<SnowWindowElement> windowList = new ArrayList<>();
    public SnowScreen(Text title) {
        super(title);
        connectElement = new ConnectElement(200,60);
        windowList.add(new TestWindow("Test", 100,100));
        windowList.add(connectElement);
    }

    @Override
    protected void init() {
        for (SnowWindowElement element : windowList) {
            element.init(element.width, element.height);
        }
    }
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        scaledWidth = ctx.getScaledWindowWidth();
        scaledHeight = ctx.getScaledWindowHeight();

        for (SnowWindowElement element : windowList) {
            element.preRender(ctx, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (SnowWindowElement element : windowList) {
            element.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (SnowWindowElement element : windowList) {
            element.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (SnowWindowElement element : windowList) {
            element.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (SnowWindowElement element : windowList) {
            element.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    //    @Override
//    public void resize(MinecraftClient client, int width, int height) {
//        for (SnowWindowElement element : windowList) {
//            element.resize(width, height);
//        }
//        super.resize(client, width, height);
//    }
}