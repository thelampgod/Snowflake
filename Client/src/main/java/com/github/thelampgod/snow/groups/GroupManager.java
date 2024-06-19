package com.github.thelampgod.snow.groups;

import com.github.thelampgod.snow.EncryptionUtil;
import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.google.common.collect.Sets;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class GroupManager {

    private final List<Group> groups = Lists.newArrayList();

    public GroupManager() {
        //testing
        groups.add(new Group("TestGroup", 0, false, Sets.newHashSet(1,2,3,4,5)));
        groups.add(new Group("Lamp's Group", 1, false, Sets.newHashSet(1,2,4,5)));
        groups.add(new Group("Entropy Group", 2, false, Sets.newHashSet(1,2,3,4,5)));
        groups.add(new Group("Epic Group", 3, true, Sets.newHashSet(1,2)));
        groups.add(new Group(":D", 4, false, Sets.newHashSet(1,5)));

    }

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
        final Path serverFolder = Paths.get(".snow", address.replaceAll(":", "\\_"));

        if (!serverFolder.toFile().exists()) {
            Files.createDirectories(serverFolder);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(serverFolder.resolve("groups.txt"))) {
            for (Group group : groups) {
                String encoded = EncryptionUtil.base64Encode(group.getPassword());

                writer.write(group.getId() + "," + encoded + "\n");
            }
        } catch (Throwable th) {
            printModMessage("Couldn't save password");
            th.printStackTrace();
        }
    }

    public void load(String address) throws IOException {
        //TODO: loadGroupsForIdentity(identity, address);
        final Path serverFolder = Paths.get(".snow", address.replaceAll(":", "\\_"));

        Path groupsPath = serverFolder.resolve("groups.txt");
        if (!groupsPath.toFile().exists()) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(groupsPath)) {
            reader.lines()
                    .map(line -> line.split(","))
                    .forEach(line -> {
                        try {
                            final int id = Integer.parseInt(line[0]);
                            final String encodedPass = line[1];
                            byte[] pass = EncryptionUtil.base64Decode(encodedPass);

                            final Group group = this.get(id);
                            if (group == null) return;
                            group.setPassword(pass);
                        } catch (Exception e) {
                            printModMessage("Couldn't parse password");
                            e.printStackTrace();
                        }
                    });
        }
    }
}
