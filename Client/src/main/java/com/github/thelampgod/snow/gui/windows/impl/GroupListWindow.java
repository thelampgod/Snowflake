package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.gui.elements.ButtonListElement;
import com.github.thelampgod.snow.gui.windows.ListWindow;
import com.github.thelampgod.snow.packets.impl.outgoing.CreateGroupPacket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;

public class GroupListWindow extends ListWindow {

    private TextFieldWidget newGroupField;

    public GroupListWindow(int width, int height) {
        super("Groups", width, height, false);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        int elementY = headerHeight;
        this.newGroupField = new TextFieldWidget(super.textRenderer, 0, elementY + textRenderer.fontHeight - 4, getWidth(), 17, Text.empty());
        this.buttonListElement = new ButtonListElement(super.textRenderer, 0, elementY += 20, height - elementY, width);

        updateButtons();
    }

    @Override
    public void updateDimensions() {
        super.updateDimensions();
        newGroupField.setWidth(getWidth());
        buttonListElement.setHeight(getHeight() - headerHeight - 20);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        if (!newGroupField.getText().isEmpty() || (focused && newGroupField.isMouseOver(mouseX - x, mouseY - y))) {
            newGroupField.render(ctx, mouseX, mouseY, delta);
        } else {
            ctx.drawTextWithShadow(textRenderer, "+ New Group", 10, headerHeight + textRenderer.fontHeight, Color.WHITE.getRGB());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int buttonId) {
        super.mouseClicked(mouseX, mouseY, buttonId);
        newGroupField.setFocused(newGroupField.mouseClicked(mouseX - x, mouseY - y, buttonId));
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        newGroupField.charTyped(chr, modifiers);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        newGroupField.keyPressed(keyCode, scanCode, modifiers);
        if (newGroupField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            Snow.getServerManager().sendPacket(new CreateGroupPacket(newGroupField.getText()));
            // await confirmation from server
            // TODO: maybe a toast or some sort of loading gif?
            newGroupField.setFocused(false);
            newGroupField.setText("");
        }
    }

    @Override
    public void updateButtons() {
        if (!hasInit) return;
        buttonListElement.clearButtons();
        final List<Group> groups = Snow.instance.getGroupManager().getGroups();
        for (final Group group : groups) {
            buttonListElement.addButton(
                    group.getName(),
                    group.getUsers().size(),
                    () -> Snow.instance.getOrCreateSnowScreen().focusWindow(group)
            );
        }
        this.width = buttonListElement.getWidth();
        this.newGroupField.setWidth(getWidth());
    }
}
