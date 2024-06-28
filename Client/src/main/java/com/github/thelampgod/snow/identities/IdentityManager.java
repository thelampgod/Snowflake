package com.github.thelampgod.snow.identities;

import com.github.thelampgod.snow.Snow;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.github.thelampgod.snow.util.Helper.mc;

public class IdentityManager {
    private final Map<String, Identity> identityMap = Maps.newHashMap();

    private final static String IDENTITIES_PATH = ".snow/identities";

    private Identity selectedIdentity;

    public IdentityManager() {
        this.load();
        String sessionName = mc.getSession().getUsername();
        if (this.select(sessionName) == null) {
            this.add(sessionName);
            this.select(sessionName);
        }
    }

    public void add(String name) {
        if (identityMap.values().stream()
                .anyMatch(identity -> identity.getName().equalsIgnoreCase(name))) {
            Snow.instance.getLog().error("Duplicate name not allowed: " + name);
            return;
        }

        try {
            identityMap.put(name.toLowerCase(), new Identity(name));
        } catch (Exception e) {
            Snow.instance.getLog().error("Failed to generate keypair: " + e.getMessage(), e);
        }
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

    public String getSelectedIdentityName() {
        return selectedIdentity.getName();
    }


    public Collection<Identity> getIdentities() {
        return identityMap.values();
    }

    public void save() throws IOException {
        Path path = Paths.get(IDENTITIES_PATH);
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }

        for (Identity identity : identityMap.values()) {
            try (FileOutputStream os = new FileOutputStream(
                    IDENTITIES_PATH + "/" + identity.getName() + ".key")) {

                os.write(identity.getPrivateKey().getEncoded());
            } catch (IOException e) {
                Snow.instance.getLog().error("Error saving identity: " + e.getMessage(), e);
            }
        }
    }

    public void load() {
        Path path = Paths.get(IDENTITIES_PATH);
        if (!path.toFile().exists()) {
            return;
        }

        File[] files = path.toFile().listFiles();
        if (files == null) return;
        Snow.instance.getLog().debug("Loading identities...");
        Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".key"))
                .forEach(keyFile -> {
                    try {
                        String name = keyFile.getName().substring(0, keyFile.getName().length() - 4);
                        byte[] keyBytes = Files.readAllBytes(keyFile.toPath());

                        Identity identity = new Identity(name, keyBytes);
                        identityMap.put(name.toLowerCase(), identity);
                        Snow.instance.getLog().debug("Loaded " + name);
                    } catch (Exception e) {
                        Snow.instance.getLog().error("Error loading identity: " + e.getMessage(), e);
                    }
                });
    }
}
