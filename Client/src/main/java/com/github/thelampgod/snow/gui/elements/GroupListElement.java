package com.github.thelampgod.snow.gui.elements;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.impl.outgoing.CreateGroupPacket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;


public class GroupListElement extends ListElement {

    private TextFieldWidget newGroupField;

    public GroupListElement(int width, int height) {
        super("Groups", width, height);
    }

    @Override
    public void init(int width, int height) {
        super.init(width, height);
        newGroupField = new TextFieldWidget(super.textRenderer, 0, headerHeight + textRenderer.fontHeight - 4, width, 17, Text.empty());

        updateButtons();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        if (!newGroupField.getText().isEmpty() || (focused && newGroupField.isMouseOver(mouseX - x, mouseY - y))) {
            newGroupField.render(ctx, mouseX, mouseY, delta);
            return;
        }
        ctx.drawTextWithShadow(textRenderer, "+ New Group", 10, headerHeight + textRenderer.fontHeight, Color.WHITE.getRGB());
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

    public void updateButtons() {
        final List<Group> groups = Snow.instance.getGroupManager().getGroups();
        for (int i = 0; i < groups.size(); ++i) {
            final Group group = groups.get(i);
            buttons.add(
                    new ListButton(
                            0,
                            headerHeight + 20 * (i + 1),
                            width, group.getName(),
                            group.getUsers().size(),
                            () -> Snow.instance.getOrCreateSnowScreen().focusWindow(group)
                    ));
        }
    }
}
