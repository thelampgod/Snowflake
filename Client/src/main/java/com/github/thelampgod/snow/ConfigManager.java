package com.github.thelampgod.snow;

import com.github.thelampgod.snow.config.IntSetting;
import com.github.thelampgod.snow.config.Setting;
import com.github.thelampgod.snow.config.StringSetting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {

    private final static Path CONFIG_PATH = Path.of(".snow", "config.txt");

    public final Map<String, Setting<?>> settings = new HashMap<>();


    public static class OptionInfo {

        public OptionInfo(String value) {
            this.value = value;
        }

        public String value;
        public Setting<?> foundSetting;
    }

    public Setting<String> lastAddress = new StringSetting(this).defaultValue("127.0.0.1:2147");
    public Setting<String> serverPassword = new StringSetting(this);
    public Setting<Integer> maxRange = new IntSetting(this).defaultValue(100_000).sliderRange(0, 60_000_000);// you kinda always have to give this

    public ConfigManager() {
        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                //if (field.getType() == this.getClass()) {
                field.setAccessible(true);
                try {
                    Setting<?> setting = (Setting<?>) field.get(this);
                    setting.name(field.getName());
                    settings.put(setting.getName(), setting);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);// yea I mean... this shouldnt happen, right?
                }
                //}
            }
            clazz = clazz.getSuperclass();
        }
        this.load();
    }

    private void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try {
                Files.readAllLines(CONFIG_PATH).stream()
                        .map(line -> line.split(","))
                        .forEach(line -> {
                            if(line.length >= 2) {
                                Setting<?> setting = settings.get(line[0]);

                                StringBuilder rebuild = new StringBuilder();
                                for (int i = 1; i < line.length; i++) {
                                    rebuild.append(line[i]);
                                    if(i + 1 < line.length){
                                        rebuild.append(",");
                                    }
                                }
                                setting.load(rebuild.toString());
                            }
                        });
            } catch (IOException e) {
                Snow.instance.getLog().error("Error reading config: " + e.getMessage(), e);
            }
            return;
        }
    }

    public void save() throws IOException {
        if (!new File(".snow").exists()) {
            Files.createDirectories(CONFIG_PATH);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_PATH.toFile()))) {
            for (Setting<?> option : settings.values()) {
                String value = option.getForSave();
                if (value != null) {
                    writer.write(option.getName() + "," + value + "\n");
                }// alert otherwise?
            }
        }
    }
}
