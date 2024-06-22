package com.github.thelampgod.snow.groups;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.identities.Identity;
import org.apache.commons.compress.utils.Lists;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GroupManager {

    private final List<Group> groups = Lists.newArrayList();

    private final static String SERVERS_PATH = ".snow/servers";

    public void clear() {
        groups.clear();
    }

    public void add(Group group) {
        groups.add(group);

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateGroupButtons();
        if (group.isOwner()) {
            screen.focusWindow(group);
        }
    }

    public void remove(Group group) {
        groups.remove(group);

        SnowScreen screen = Snow.instance.getOrCreateSnowScreen();
        screen.updateGroupButtons();
        screen.removeGroupWindow(group);
    }

    public Group get(int groupId) {
        return groups.stream()
                .filter(group -> group.getId() == groupId)
                .findAny()
                .orElse(null);
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void save(String address) throws IOException {
        final Path serverFolder = Paths.get(SERVERS_PATH, address.replaceAll(":", "\\_"));

        if (!serverFolder.toFile().exists()) {
            Files.createDirectories(serverFolder);
        }
        final Identity selectedIdentity = Snow.instance.getIdentityManager().getSelectedIdentity();
        if (selectedIdentity == null) return;

        try (FileOutputStream os = new FileOutputStream(serverFolder.resolve(selectedIdentity.getName()).toFile())) {
            StringBuilder builder = new StringBuilder();
            for (Group group : groups) {
                String groupEntry = group.getId() + "," + new String(group.getPassword());

                builder.append(groupEntry).append("\n");
            }
            byte[] encrypted = EncryptionUtil.encrypt(builder.toString().getBytes(), selectedIdentity.getPublicKey());
            os.write(encrypted);
        } catch (Exception e) {
            Snow.instance.getLog().error("Error encrypting group passwords " + e.getMessage(), e);
        }
    }

    public void load(String address) throws Exception {
        final Path serverFolder = Paths.get(SERVERS_PATH, address.replaceAll(":", "\\_"));

        final Identity selectedIdentity = Snow.instance.getIdentityManager().getSelectedIdentity();
        if (selectedIdentity == null) return;

        Path groupsPath = serverFolder.resolve(selectedIdentity.getName());
        if (!groupsPath.toFile().exists()) {
            return;
        }

        byte[] fileBytes = EncryptionUtil.decrypt(Files.readAllBytes(groupsPath), selectedIdentity.getPrivateKey());

        String[] lines = new String(fileBytes).split("\n");
        for (String line : lines) {
            String[] split = line.split(",");

            final int id = Integer.parseInt(split[0]);
            final String pass = split[1];

            final Group group = this.get(id);
            if (group == null) return;
            group.setPassword(pass.getBytes());
        }
    }
}
