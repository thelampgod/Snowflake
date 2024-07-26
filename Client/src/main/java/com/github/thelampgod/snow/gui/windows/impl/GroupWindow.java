package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.SnowConfirmScreen;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.packets.impl.outgoing.GroupLeavePacket;

import java.awt.*;

import static com.github.thelampgod.snow.util.Helper.mc;

public class GroupWindow extends ChatWindow {
    private final Group group;

    // Crown character https://graphemica.com/%F0%9F%91%91
    private final static String CROWN = "\uD83D\uDC51";

    private TextButton locationShareButton;

    public GroupWindow(Group group) {
        super(group.getId(), true, (group.isOwner() ? CROWN + " " : "") + group.getName(), 200, 100);
        this.group = group;
    }

    public int getId() {
        return group.getId();
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        addHeaderButton("L", 7, "Leave Group",
                () -> Snow.getServerManager().sendPacket(new GroupLeavePacket(this.group.getId())));
        if (group.isOwner()) {
            addHeaderButton("+", 7, "Add User to Group",
                    () -> Snow.instance.getOrCreateSnowScreen().focusWindow(getAddWindow(this.group)));
            addHeaderButton("-", 7, "Remove User from Group",
                    () -> Snow.instance.getOrCreateSnowScreen().focusWindow(getRemoveWindow(this.group)));
        }

        locationShareButton = addHeaderButton("!", 7, "Start Location Share", Color.RED,
                () -> this.toggleSharing(group));

        if (Snow.instance.getSharer().getSelectedGroup() == null) return;
        if (this.getId() == Snow.instance.getSharer().getSelectedGroup().getId()) {
            locationShareButton.setColor(Color.GREEN.getRGB());
            locationShareButton.setTooltip("Stop Location Share");
        }
    }

    private void toggleSharing(Group group) {
        if (locationShareButton.getColor() == Color.RED.getRGB()) {
            mc.setScreen(new SnowConfirmScreen("Start location share with \"" + this.group.getName() + "\"?",
                    () -> this.startSharingTo(group)));
            return;
        }

        stopSharing();
    }

    private void stopSharing() {
        Snow.instance.getSharer().clear();
        updateShareButton();
    }

    public void updateShareButton() {
        locationShareButton.setColor(Color.RED.getRGB());
        locationShareButton.setTooltip("Start Location Share");
    }

    private void startSharingTo(Group group) {
        Snow.instance.getSharer().select(group);
        mc.setScreen(Snow.instance.getOrCreateSnowScreen());
        locationShareButton.setColor(Color.GREEN.getRGB());
        locationShareButton.setTooltip("Stop Location Share");

        // Toggle all other groups share buttons off
        for (int i = 0; i < SnowScreen.windowList.size(); ++i) {
            if (SnowScreen.windowList.get(i) instanceof GroupWindow window) {
                if (window.equals(this)) continue;
                window.updateShareButton();
            }
        }
    }

    private UserRemoveFromGroupListWindow getRemoveWindow(Group group) {
        for (SnowWindow w : SnowScreen.windowList) {
            if (w instanceof UserRemoveFromGroupListWindow listWindow) {
                if (listWindow.getGroupId() == group.getId()) {
                    return listWindow;
                }
            }
        }

        return new UserRemoveFromGroupListWindow(this.group, 150, 200);
    }

    private UserAddToGroupListWindow getAddWindow(Group group) {
        for (SnowWindow w : SnowScreen.windowList) {
            if (w instanceof UserAddToGroupListWindow listWindow) {
                if (listWindow.getGroupId() == group.getId()) {
                    return listWindow;
                }
            }
        }

        return new UserAddToGroupListWindow(this.group, 150, 200);
    }
}
