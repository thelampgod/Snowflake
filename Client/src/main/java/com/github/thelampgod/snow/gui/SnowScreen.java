package com.github.thelampgod.snow.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class SnowScreen extends Screen {
    public static int scaledWidth;
    public static int scaledHeight;
    public static List<SnowWindow> windowList = new ArrayList<>();

    public SnowScreen(Text title) {
        super(title);
        windowList.add(new TestElement("Test", 100, 100));
        windowList.add(new ConnectElement(200, 60));
        windowList.add(new GroupListElement(150, 200));
    }

    @Override
    protected void init() {
        for (SnowWindow element : windowList) {
            element.init(element.width, element.height);
        }
    }

    public static void focusWindow(SnowWindow window) {
        for (SnowWindow win : windowList) {
            win.focused = false;
        }
        window.focused = true;
        windowList.removeIf(it -> it == window);
        windowList.add(window);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        scaledWidth = ctx.getScaledWindowWidth();
        scaledHeight = ctx.getScaledWindowHeight();

        for (SnowWindow element : windowList) {
            element.preRender(ctx, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (SnowWindow element : windowList) {
            element.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (SnowWindow element : windowList) {
            element.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = windowList.size() - 1; i >= 0; i--) {
            SnowWindow liveWindow = windowList.get(i);
            if (liveWindow.cursorInWindow(mouseX, mouseY)) {
                if (i != windowList.size() - 1) {
                    windowList.remove(i);
                    windowList.add(liveWindow);
                }
                break;
            }
        }

        windowList.get(windowList.size() - 1).mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (SnowWindow element : windowList) {
            element.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (SnowWindow element : windowList) {
            element.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    //    @Override
//    public void resize(MinecraftClient client, int width, int height) {
//        for (SnowWindowElement element : windowList) {
//            element.resize(width, height);
//        }
//        super.resize(client, width, height);
//    }
}
