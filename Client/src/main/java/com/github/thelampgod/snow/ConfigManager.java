package com.github.thelampgod.snow;

import com.google.common.collect.Maps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigManager {

    private final static Path CONFIG_PATH = Path.of(".snow","config.txt");

    private final Map<String, String> options = Maps.newHashMap();

    public ConfigManager() {
        this.load();
    }

    public <T> void addOption(String option, T value) {
        this.addOption(option, value.toString());
    }

    public void addOption(String option, String value) {
        options.put(option, value);
    }

    public String getOption(String option) {
        return options.get(option);
    }

    private void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try {
                Files.readAllLines(CONFIG_PATH).stream()
                        .map(line -> line.split(","))
                        .forEach(line -> {
                            options.put(line[0], line.length < 2 ? "" : line[1]);
                        });
            } catch (IOException e) {
                Snow.instance.getLog().error("Error reading config: " + e.getMessage(), e);
            }
            return;
        }
        // Defaults
        this.addOption("maxRange", 100_000);
        this.addOption("lastAddress", "127.0.0.1:2147");
    }

    public void save() throws IOException {
        if (!new File(".snow").exists()) {
            Files.createDirectories(CONFIG_PATH);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_PATH.toFile()))) {
            for (Map.Entry<String, String> option : options.entrySet()) {
                writer.write(option.getKey() + "," + option.getValue() + "\n");
            }
        }
    }
}
