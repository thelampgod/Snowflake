package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.gui.windows.impl.*;
import com.github.thelampgod.snow.users.User;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.thelampgod.snow.util.Helper.mc;

public class SnowScreen extends Screen {
    public static int scaledWidth;
    public static int scaledHeight;
    public static List<SnowWindow> windowList = new ArrayList<>();

    public GroupListWindow groupListWindow;
    public UserListWindow userListWindow;

    private ConnectWindow connectWindow;

    public SnowScreen(Text title) {
        super(title);
        connectWindow = new ConnectWindow(100, 60);
        windowList.add(connectWindow);
        groupListWindow = new GroupListWindow(150, 210);
        windowList.add(groupListWindow);
        userListWindow = new UserListWindow(150, 210);
        windowList.add(userListWindow);
    }

    public void clear() {
        for (int i = windowList.size() - 1; i >= 0; --i) {
            SnowWindow window = windowList.get(i);

            if (!(window instanceof ConnectWindow
                    || window instanceof GroupListWindow
                    || window instanceof UserListWindow)) {
                this.remove(window);
                continue;
            }

            window.clear();
        }
    }

    @Override
    protected void init() {
        this.setScale();
        for (SnowWindow element : windowList) {
            if (!element.hasInit) {
                element.init(element.getWidth(), element.getHeight());
            }
        }
    }

    private void setScale() {
        scaledWidth = mc.getWindow().getScaledWidth();
        scaledHeight = mc.getWindow().getScaledHeight();
    }

    public void focusWindow(User user) {
        for (SnowWindow win : windowList) {
            if (win instanceof UserWindow e) {
                if (e.getId() == user.getId()) {
                    focusWindow(e);
                    return;
                }
            }
        }
        focusWindow(new UserWindow(user));
    }

    public void focusWindow(Group group) {
        for (SnowWindow win : windowList) {
            if (win instanceof GroupWindow e) {
                if (e.getId() == group.getId()) {
                    focusWindow(e);
                    return;
                }
            }
        }
        focusWindow(new GroupWindow(group));
    }

    public void focusWindow(SnowWindow window) {
        for (SnowWindow win : windowList) {
            win.focused = false;
        }
        window.focused = true;
        windowList.removeIf(it -> it == window);
        windowList.add(window);
        if (!window.hasInit) {
            window.init(window.getWidth(), window.getHeight());
        }
    }

    public void remove(SnowWindow window) {
        windowList.remove(window);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        setScale();
        boolean connected = Snow.getServerManager().isConnected();
        //TODO: Green / red circle
        ctx.drawText(textRenderer, (connected ? "Connected" : "Disconnected"), 10, scaledHeight - 10, Color.WHITE.getRGB(), true);

        ctx.getMatrices().push();
        RenderSystem.depthMask(false);
        if (!connected) {
            focusWindow(connectWindow);
            connectWindow.preRender(ctx, mouseX, mouseY, delta);
            return;
        }


        for (int i = 0; i < windowList.size(); ++i) {
            SnowWindow window = windowList.get(i);
            window.preRender(ctx, mouseX, mouseY, delta);
        }
        RenderSystem.depthMask(true);
        ctx.getMatrices().pop();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (int i = 0; i < windowList.size(); ++i) {
            windowList.get(i).keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (int i = 0; i < windowList.size(); ++i) {
            windowList.get(i).charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!Snow.getServerManager().isConnected()) {
            connectWindow.mouseClicked(mouseX, mouseY, button);
            return true;
        }

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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (SnowWindow element : windowList) {
            element.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public void clearShareStatus() {
        for (int i = 0; i < windowList.size(); ++i) {
            if (windowList.get(i) instanceof GroupWindow window) {
                window.updateShareButton();
            }
        }
    }

    public void updateUserButtons() {
        userListWindow.updateButtons();
    }

    public void updateGroupButtons() {
        groupListWindow.updateButtons();
    }

    public void removeGroupWindow(Group group) {
        for (int i = windowList.size() - 1; i >= 0; i--) {
            SnowWindow window = windowList.get(i);
            if (window instanceof GroupWindow element) {
                if (element.getId() == group.getId()) {
                    remove(element);
                }
            }
        }
    }

    public void removeUserWindow(User user) {
        for (int i = windowList.size() - 1; i >= 0; i--) {
            SnowWindow window = windowList.get(i);
            if (window instanceof UserWindow element) {
                if (element.getId() == user.getId()) {
                    remove(element);
                }
            }
        }
    }

    public void addMessage(int sender, String message) {
        final User user = Snow.instance.getUserManager().get(sender);

        for (SnowWindow window : windowList) {
            if (window instanceof UserWindow userWindow) {
                if (!(userWindow.getId() == user.getId())) continue;
                focusWindow(userWindow);
                userWindow.addMessage(user.getName(), message);
                return;
            }
        }

        UserWindow window = new UserWindow(user);
        focusWindow(window);
        window.addMessage(user.getName(), message);
    }

    public void addMessage(int groupId, int sender, String message) {
        final User user = Snow.instance.getUserManager().get(sender);

        GroupWindow window = getOrCreateGroupWindow(groupId);
        focusWindow(window);
        window.addMessage(user.getName(), message);
    }

    private GroupWindow getOrCreateGroupWindow(int groupId) {
        for (SnowWindow window : windowList) {
            if (window instanceof GroupWindow groupWindow) {
                if ((groupWindow.getId() == groupId)) return groupWindow;
            }
        }

        return new GroupWindow(Snow.instance.getGroupManager().get(groupId));
    }

    public void groupJoin(int groupId, int userId) {
        GroupWindow window = getOrCreateGroupWindow(groupId);
        focusWindow(window);
        window.addMessage(Snow.instance.getUserManager().get(userId).getName() + " joined the group.", Color.GREEN);
    }

    public void groupLeave(int groupId, int userId) {
        GroupWindow window = getOrCreateGroupWindow(groupId);
        focusWindow(window);
        window.addMessage(Snow.instance.getUserManager().get(userId).getName() + " left the group.", Color.RED);
    }
}
