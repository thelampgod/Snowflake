package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Helper;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.gui.elements.DropdownListElement;
import com.github.thelampgod.snow.gui.elements.SnowButton;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.identities.Identity;
import com.github.thelampgod.snow.identities.IdentityManager;
import com.github.thelampgod.snow.packets.impl.outgoing.LoginStartPacket;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;

import static com.github.thelampgod.snow.Helper.mc;

public class ConnectWindow extends SnowWindow {

    private TextFieldWidget ipField;
    private TextFieldWidget maxRangeField;
    private SnowButton connectButton;
    private DropdownListElement identitiesDropdown;

    private int maxRange;

    public ConnectWindow(int width, int height) {
        super("Connect", width, height, false);
    }

    public void init(int width, int height) {
        super.init(getWidth(), getHeight());

        this.maxRange = Integer.parseInt(Snow.instance.getOption("maxRange"));

        int x = (width - 100) / 2;
        this.ipField = new TextFieldWidget(textRenderer, x, 0, 100, 17, Text.empty());
        this.ipField.setPlaceholder(Text.of("127.0.0.1:2147"));
        this.ipField.setMaxLength(256);
        this.ipField.setFocused(true);
        this.ipField.setText(Snow.instance.getOption("lastAddress"));

        this.maxRangeField = new TextFieldWidget(textRenderer, x, 17, 100, 17, Text.empty());
        this.maxRangeField.setPlaceholder(Text.of("Max Range..."));
        this.maxRangeField.setEditableColor(Color.WHITE.getRGB());
        this.maxRangeField.setUneditableColor(Color.GRAY.getRGB());
        this.maxRangeField.setMaxLength(9);
        this.maxRangeField.setText(""+maxRange);

        this.identitiesDropdown = new DropdownListElement(textRenderer, x, 17 * 2, 100, 17);
        IdentityManager identities = Snow.instance.getIdentityManager();
        for (Identity identity : identities.getIdentities()) {
            this.identitiesDropdown.addOption(identity.getName(), () -> identities.select(identity.getName()));
        }

        this.connectButton = new SnowButton(textRenderer, "Connect", x, 17 * 3, 100, 17, this::connect);
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        connectButton.render(ctx, (int) (mouseX - x), (int) (mouseY - y), delta);
        connectButton.setTitle("Disconnect");

        maxRangeField.render(ctx, mouseX, mouseY, delta);

        if (!Snow.getServerManager().isConnected()) {
            // Move window to the center of the screen
            this.x = (SnowScreen.scaledWidth - this.width) / 2;
            this.y = (SnowScreen.scaledHeight - this.height) / 2;
            ipField.render(ctx, mouseX, mouseY, delta);
            identitiesDropdown.render(ctx, (int) (mouseX - x), (int) (mouseY - y), delta);
            connectButton.setTitle("Connect");
        } else {
            // Move window to the bottom right corner of the screen
            this.x = SnowScreen.scaledWidth - width - 3;
            this.y = SnowScreen.scaledHeight - height - 17 - 3;
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        if (!focused) return;
        ipField.keyPressed(keyCode, scanCode, modifiers);
        maxRangeField.keyPressed(keyCode, scanCode, modifiers);

        if (this.ipField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.connect();
        }
    }

    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        if (!focused) return;
        ipField.charTyped(chr, modifiers);
        maxRangeField.charTyped(chr, modifiers);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (identitiesDropdown.mouseClicked(mouseX - x, mouseY - y, button)) return;
        connectButton.mouseClicked(mouseX - x, mouseY - y, button);

        ipField.setFocused(ipField.mouseClicked(mouseX - x, mouseY - y, button));
        maxRangeField.setFocused(maxRangeField.mouseClicked(mouseX - x, mouseY - y, button));
    }

    private void connect() {
        if (!Snow.getServerManager().isConnected()) {
            Snow.instance.connect(ipField.getText());

            try {
                maxRange = Integer.parseInt(maxRangeField.getText());
                Snow.instance.getConfigManager().addOption("maxRange", maxRange);
            } catch (NumberFormatException e) {
                Helper.addToast("Failed to parse", "Couldn't parse max range: " + maxRangeField.getText());
                Snow.instance.getLog().error("Failed to parse range: " + e.getMessage(), e);
                maxRangeField.setText(""+maxRange);
            }

            Snow.getServerManager().sendPacket(new LoginStartPacket(
                    EncryptionUtil.asciiArmored(Helper.getPublicKey())));
            return;
        }
        Snow.getServerManager().close();
    }
}
