package com.github.thelampgod.snow.config;

public class StringSetting extends Setting<String> {
    public StringSetting(Object owner) {
        super(owner, "", s -> s, s -> s);
    }
}
