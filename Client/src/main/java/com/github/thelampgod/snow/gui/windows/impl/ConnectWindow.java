package com.github.thelampgod.snow.gui.windows.impl;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.gui.elements.DropdownListElement;
import com.github.thelampgod.snow.gui.elements.SnowButton;
import com.github.thelampgod.snow.gui.windows.SnowWindow;
import com.github.thelampgod.snow.identities.Identity;
import com.github.thelampgod.snow.identities.IdentityManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import static com.github.thelampgod.snow.Helper.mc;


public class ConnectWindow extends SnowWindow {


    //TODO: actually save last ip
    public final static String savedIp = "127.0.0.1:2147";

    private TextFieldWidget ipField;
    private SnowButton connectButton;
    private DropdownListElement identitiesDropdown;

    public ConnectWindow(int width, int height) {
        super("Connect", width, height, false);
    }

    public void init(int width, int height) {
        super.init(getWidth(), getHeight());

        int x = (width - 100) / 2;
        int y = height / 2;
        this.ipField = new TextFieldWidget(textRenderer, x, y - 8, 100, 17, Text.empty());
        this.ipField.setMaxLength(256);
        this.ipField.setFocused(true);
        this.ipField.setText("127.0.0.1:2147");
        this.connectButton = new SnowButton(textRenderer, "Connect", x,y + 9,100,17, this::connect);
        this.identitiesDropdown = new DropdownListElement(textRenderer, x, y + 9 + 17, 100, 17);
        IdentityManager identities = Snow.instance.getIdentityManager();
        for (Identity identity : identities.getIdentities()) {
            this.identitiesDropdown.addOption(identity.getName(), () -> identities.select(identity.getName()));
        }
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        connectButton.setTitle("Disconnect");
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
            this.y = SnowScreen.scaledHeight - height - 3;
        }

        connectButton.render(ctx, (int) (mouseX - x), (int) (mouseY - y), delta);
    }


    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        if (!focused) return;
        ipField.keyPressed(keyCode, scanCode, modifiers);

        if (this.ipField.isFocused() && (keyCode == 257 || keyCode == 335)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            Snow.instance.connect(ipField.getText());
        }
    }

    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);
        if (!focused) return;
        ipField.charTyped(chr, modifiers);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        connectButton.mouseClicked(mouseX - x, mouseY - y, button);
        identitiesDropdown.mouseClicked(mouseX - x, mouseY - y, button);
    }


    private void connect() {
        if (!Snow.getServerManager().isConnected()) {
            Snow.instance.connect(ipField.getText());
            return;
        }
        Snow.getServerManager().close();
    }
}
