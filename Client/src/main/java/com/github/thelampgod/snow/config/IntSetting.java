package com.github.thelampgod.snow.config;

public class IntSetting extends Setting<Integer> {
    public IntSetting(Object owner) {
        super(owner, 0, Integer::parseInt, String::valueOf);
    }


}
