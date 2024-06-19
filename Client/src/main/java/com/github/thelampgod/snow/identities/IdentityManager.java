package com.github.thelampgod.snow.identities;

import com.github.thelampgod.snow.Snow;
import com.google.common.collect.Maps;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class IdentityManager {
    private final Map<String, Identity> identityMap = Maps.newHashMap();

    private final static String IDENTITIES_PATH = ".snow/identities";

    private Identity selectedIdentity;

    public IdentityManager() {
        this.load();
    }

    public void add(String name) throws Exception {
        if (identityMap.values().stream()
                .anyMatch(identity -> identity.getName().equalsIgnoreCase(name))) {
            throw new Exception("Duplicate name not allowed");
        }

        identityMap.put(name.toLowerCase(), new Identity(name));
    }

    public void remove(String name) {
        identityMap.remove(name);
    }

    public Identity select(String name) {
        this.selectedIdentity = identityMap.get(name.toLowerCase());
        return selectedIdentity;
    }

    public Identity getSelectedIdentity() {
        return selectedIdentity;
    }

    public void save() throws IOException {
        Path path = Paths.get(IDENTITIES_PATH);
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }

        for (Map.Entry<String, Identity> entry : identityMap.entrySet()) {
            try (FileOutputStream os = new FileOutputStream(
                    IDENTITIES_PATH + "/" + entry.getKey() + ".key")) {

                os.write(entry.getValue().getPrivateKey().getEncoded());
            } catch (IOException e) {
                Snow.instance.getLog().error("Error saving identity " + e.getMessage(), e);
            }
        }
    }

    public void load() {
        Path path = Paths.get(IDENTITIES_PATH);
        if (!path.toFile().exists()) {
            return;
        }

        Snow.instance.getLog().debug("Loading identities...");
        Arrays.stream(path.toFile().listFiles())
                .filter(file -> file.getName().endsWith(".key"))
                .forEach(keyFile -> {
                    try {
                        String name = keyFile.getName();
                        byte[] keyBytes = Files.readAllBytes(keyFile.toPath());

                        Identity identity = new Identity(name, keyBytes);
                        identityMap.put(name, identity);
                        Snow.instance.getLog().debug("Loaded " + name);
                    } catch (Exception e) {
                        Snow.instance.getLog().error("Error loading identity " + e.getMessage(), e);
                    }
                });
    }
}
