package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.gui.elements.DropdownListElement;
import com.github.thelampgod.snow.gui.elements.SnowButton;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.identities.Identity;
import com.github.thelampgod.snow.identities.IdentityManager;
import com.github.thelampgod.snow.packets.impl.outgoing.LoginStartPacket;
import com.github.thelampgod.snow.util.EncryptionUtil;
import com.github.thelampgod.snow.util.Helper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.awt.*;

import static com.github.thelampgod.snow.util.Helper.mc;

public class ConnectWindow extends SnowWindow {

    private TextFieldWidget ipField;
    private TextFieldWidget serverPassField;
    private SnowButton connectButton;
    private DropdownListElement identitiesDropdown;


    public ConnectWindow(int width, int height) {
        super("Connect", width, height, false);
    }

    public void init(int width, int height) {
        super.init(getWidth(), getHeight());

        int x = (width - 100) / 2;
        this.ipField = new TextFieldWidget(textRenderer, x, -17 * 3, 100, 17, Text.empty());
        this.ipField.setPlaceholder(Text.of("127.0.0.1:2147"));
        this.ipField.setMaxLength(256);
        this.ipField.setFocused(true);
        this.ipField.setText(Snow.instance.getConfig().lastAddress.get());

        this.serverPassField = new TextFieldWidget(textRenderer, x, -17 * 2, 100, 17, Text.empty());
        this.serverPassField.setPlaceholder(Text.of("Server Password..."));
        this.serverPassField.setMaxLength(256);
        this.serverPassField.setText(Snow.instance.getConfig().serverPassword.get());

        this.identitiesDropdown = new DropdownListElement(textRenderer, x, 17 * 2, 100, 17);
        IdentityManager identities = Snow.instance.getIdentityManager();
        for (Identity identity : identities.getIdentities()) {
            this.identitiesDropdown.addOption(identity.getName(), () -> identities.select(identity.getName()));
        }

        this.connectButton = new SnowButton(textRenderer, "Connect", x, 17 * 3, 100, 17, this::connect);
    }

    public boolean render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        setFieldColor(ipField);
        setFieldColor(serverPassField);

        connectButton.render(ctx, (int) (mouseX - x), (int) (mouseY - y), delta);
        connectButton.setTitle("Disconnect");
        int centerX = (getWidth()) / 2;

        if (!Snow.getServerManager().isConnected()) {
            // Move window to the center of the screen
            this.x = (SnowScreen.scaledWidth - this.width) / 2;
            this.y = (SnowScreen.scaledHeight - this.height) / 2;
            ctx.drawCenteredTextWithShadow(textRenderer, "Server address:", centerX, -17 * 3 - 10, Color.WHITE.getRGB());
            ipField.render(ctx, mouseX, mouseY, delta);

            String temp = serverPassField.getText();
            serverPassField.setText("*".repeat(temp.length()));
            serverPassField.render(ctx, mouseX, mouseY, delta);
            serverPassField.setText(temp);

            ctx.drawCenteredTextWithShadow(textRenderer, "Identities:", centerX, 17 * 2 - 10, Color.WHITE.getRGB());
            identitiesDropdown.render(ctx, (int) (mouseX - x), (int) (mouseY - y), delta);
            connectButton.setTitle("Connect");
        } else {
            // Move window to the bottom right corner of the screen
            this.x = SnowScreen.scaledWidth - width - 3;
            this.y = SnowScreen.scaledHeight - height - 17 - 3;
        }
        return true;
    }

    private void setFieldColor(TextFieldWidget field) {
        if (field.getText().isEmpty()) {
            field.setEditableColor(Color.GRAY.getRGB());
        } else {
            field.setEditableColor(Color.WHITE.getRGB());
        }
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        if (!focused) return;
        ipField.keyPressed(keyCode, scanCode, modifiers);
        serverPassField.keyPressed(keyCode, scanCode, modifiers);

        if (this.ipField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.connect();
        }
    }

    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        if (!focused) return;
        ipField.charTyped(chr, modifiers);
        serverPassField.charTyped(chr, modifiers);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (identitiesDropdown.mouseClicked(mouseX - x, mouseY - y, button)) return;
        connectButton.mouseClicked(mouseX - x, mouseY - y, button);

        ipField.setFocused(ipField.mouseClicked(mouseX - x, mouseY - y, button));
        serverPassField.setFocused(serverPassField.mouseClicked(mouseX - x, mouseY - y, button));
    }

    private void connect() {
        if (!Snow.getServerManager().isConnected()) {
            Snow.instance.connect(ipField.getText(), serverPassField.getText());


            Snow.getServerManager().sendPacket(new LoginStartPacket(
                    EncryptionUtil.asciiArmored(Helper.getPublicKey())));
            return;
        }
        Snow.getServerManager().close();
    }
}
