package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.elements.*;
import com.github.thelampgod.snow.users.User;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.github.thelampgod.snow.Helper.mc;

public class SnowScreen extends Screen {
    public static int scaledWidth;
    public static int scaledHeight;
    public static List<SnowWindow> windowList = new ArrayList<>();

    public GroupListElement groupListElement;

    public SnowScreen(Text title) {
        super(title);
        groupListElement = new GroupListElement(150, 200);
        windowList.add(new ConnectElement(200, 60));
        windowList.add(groupListElement);
        windowList.add(new UserListElement(150, 200));
    }

    @Override
    protected void init() {
        this.setScale();
        for (SnowWindow element : windowList) {
            if (!element.hasInit) {
                element.init(element.width, element.height);
            }
        }
    }

    private void setScale() {
        scaledWidth = mc.getWindow().getScaledWidth();
        scaledHeight = mc.getWindow().getScaledHeight();
    }

    public void focusWindow(User user) {
        for (SnowWindow win : windowList) {
            if (win instanceof UserElement e) {
                if (e.getId() == user.getId()) {
                    focusWindow(e);
                    return;
                }
            }
        }
        focusWindow(new UserElement(user));
    }

    public void focusWindow(Group group) {
        for (SnowWindow win : windowList) {
            if (win instanceof GroupElement e) {
                if (e.getId() == group.getId()) {
                    focusWindow(e);
                    return;
                }
            }
        }
        focusWindow(new GroupElement(group));
    }

    public void focusWindow(SnowWindow window) {
        for (SnowWindow win : windowList) {
            win.focused = false;
        }
        window.focused = true;
        windowList.removeIf(it -> it == window);
        windowList.add(window);
        if (!window.hasInit) {
            window.init(window.width, window.height);
        }
    }

    public void remove(SnowWindow window) {
        windowList.remove(window);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        setScale();
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

    public void addGroup(Group group) {
        groupListElement.addGroup(group);
    }

    public void addToast(String s) {
        mc.getToastManager().add(new SystemToast(SystemToast.Type.NARRATOR_TOGGLE, Text.literal(s), null));
    }
}
