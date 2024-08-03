package com.github.thelampgod.snow.gui;

import com.github.thelampgod.snow.ConfigManager;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.config.Setting;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {
    public SettingsScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        for (Setting<?> setting  : Snow.instance.getConfigManager().settings.values()) {
             //= info.foundSetting;

            Snow.instance.getLog().info(setting.getName());
        }
    }
}