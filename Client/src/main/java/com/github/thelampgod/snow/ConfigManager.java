package com.github.thelampgod.snow;

import com.github.thelampgod.snow.config.IntSetting;
import com.github.thelampgod.snow.config.Setting;
import com.github.thelampgod.snow.config.StringSetting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final static Path CONFIG_PATH = Path.of(".snow", "config.txt");

    private final Map<String, OptionInfo> optionCache = new HashMap<>();

    public Collection<OptionInfo> getOptions() {
        return optionCache.values();
    }

    public <T> String giveSettingReference(String name, Setting<T> tSetting) {
        // ideally this is saved more suffisticated and says what type of value it is...
        OptionInfo info = optionCache.computeIfAbsent(name, k -> new OptionInfo(tSetting.getDefaultAsString()));
        info.foundSetting = tSetting;
        return info.value;
    }

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
        this.load();
    }

    private void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try {
                Files.readAllLines(CONFIG_PATH).stream()
                        .map(line -> line.split(","))
                        .forEach(line -> {
                            optionCache.put(line[0], new OptionInfo(line.length < 2 ? "" : line[1]));
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
            for (Map.Entry<String, OptionInfo> option : optionCache.entrySet()) {
                OptionInfo info = option.getValue();
                String value = info.value;
                if (info.foundSetting != null) {
                    value = info.foundSetting.getForSave();
                }// alert otherwise?
                writer.write(option.getKey() + "," + value + "\n");
            }
        }
    }
}
