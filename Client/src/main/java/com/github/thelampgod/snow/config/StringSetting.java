package com.github.thelampgod.snow.config;

import com.github.thelampgod.snow.ConfigManager;

public class StringSetting extends Setting<String> {
    public StringSetting(ConfigManager owner) {
        super(owner, "", s -> s, s -> s);
    }
}
