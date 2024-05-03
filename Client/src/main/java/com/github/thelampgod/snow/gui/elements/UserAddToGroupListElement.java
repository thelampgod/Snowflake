//package com.github.thelampgod.snow.gui.elements;
//
//import com.github.thelampgod.snow.EncryptionUtil;
//import com.github.thelampgod.snow.Helper;
//import com.github.thelampgod.snow.Snow;
//import com.github.thelampgod.snow.groups.Group;
//import com.github.thelampgod.snow.packets.impl.outgoing.GroupInvitePacket;
//import com.github.thelampgod.snow.users.User;
//
//import java.util.List;
//
//import static com.github.thelampgod.snow.Helper.printModMessage;
//
//public class UserAddToGroupListElement extends ListElement {
//    private final Group group;
//    public UserAddToGroupListElement(Group group, int width, int height) {
//        super("Add to: " + group.getName(), width, height, true);
//        this.group = group;
//    }
//
//    @Override
//    public void init(int width, int height) {
//        super.init(width, height);
//        final List<User> users = Snow.instance.getUserManager().getUsers();
//        int j = 0;
//        for (final User user : users) {
//            if (group.containsUser(user.getId())) continue;
//            buttons.add(
//                    new ListButton(
//                            0,
//                            headerHeight + 20 * j,
//                            width, user.getName(),
//                            () -> {
//                                sendInvite(user, group);
//                                Snow.instance.getOrCreateSnowScreen().remove(this);
//                            }
//                    ));
//            ++j;
//        }
//
//        if (j == 0) {
//            Helper.addToast("No one to add!");
//            Snow.instance.getOrCreateSnowScreen().remove(this);
//        }
//    }
//
//    private void sendInvite(User user, Group group) {
//        try {
//            byte[] encryptedGroupPassword = EncryptionUtil.encrypt(group.getPassword(), user.getKey());
//            Snow.getServerManager().sendPacket(new GroupInvitePacket(user.getId(), group.getId(), encryptedGroupPassword));
//        } catch (Exception e) {
//            printModMessage("Failed to send group password");
//            e.printStackTrace();
//        }
//    }
//}
