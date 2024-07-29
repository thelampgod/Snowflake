package com.github.thelampgod.snow.config;

import com.github.thelampgod.snow.ConfigManager;

public class IntSetting extends Setting<Integer> {
    public IntSetting(ConfigManager owner) {
        super(owner, 0, Integer::parseInt, String::valueOf);
    }


}
