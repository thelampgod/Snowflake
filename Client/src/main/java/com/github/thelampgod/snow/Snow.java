package com.github.thelampgod.snow;

import com.github.thelampgod.snow.commands.impl.AuthenticateCommand;
import com.github.thelampgod.snow.groups.GroupManager;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.identities.IdentityManager;
import com.github.thelampgod.snow.render.WaypointRenderer;
import com.github.thelampgod.snow.users.UserManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


@Environment(EnvType.CLIENT)
public class Snow implements ModInitializer {
    public static Snow instance;
    private final Logger LOGGER = LogManager.getLogger("Snow");

    private static String IP = "127.0.0.1";
    private static int PORT = 2147;
    private static ServerManager serverManager;

    private GroupManager groupManager;
    private UserManager userManager;
    private WaypointRenderer renderer;
    private IdentityManager identityManager;
    private SnowScreen snowScreen;
    @Override
    public void onInitialize() {
        LOGGER.info("Loading...");
        long now = System.currentTimeMillis();
        init();
        LOGGER.info("Loaded in {}ms", System.currentTimeMillis() - now);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "shutdown_thread"));
    }

    private void init() {
        instance = this;
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(AuthenticateCommand.register());
        });

        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.snow.opengui", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.snow.gui" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(getOrCreateSnowScreen());
            }
        });

        identityManager = new IdentityManager();
        renderer = new WaypointRenderer();
        groupManager = new GroupManager();
        userManager = new UserManager();
    }

    public SnowScreen getOrCreateSnowScreen() {
        if (snowScreen == null) {
            snowScreen = new SnowScreen(Text.literal("Snow"));
        }
        return snowScreen;
    }

    private void shutdown() {
        LOGGER.info("Shutting down...");
        long now = System.currentTimeMillis();
        try {
            identityManager.save();
            if (serverManager != null) {
                serverManager.close();
            }
        } catch (Exception e) {
            this.getLog().error("Error creating directories " + e.getMessage(), e);
        }
        LOGGER.info("Shutdown in {}ms", System.currentTimeMillis() - now);
    }

    public Logger getLog() {
        return LOGGER;
    }

    public static synchronized ServerManager getServerManager() {
        if (serverManager == null) {
            serverManager = new ServerManager(IP, PORT);
        }
        return serverManager;
    }

    public GroupManager getGroupManager() {
        return groupManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public WaypointRenderer getRenderer() {
        return renderer;
    }

    public IdentityManager getIdentityManager() {
        return identityManager;
    }

    public void connect(String address) {
        if (serverManager != null) {
            serverManager.close();
        }
        System.out.println("Connecting to " + address);

        try {
            String[] parts = address.split(":");
            if (parts.length < 2) return;
            serverManager = new ServerManager(parts[0], Integer.parseInt(parts[1]));
            serverManager.connect();
        } catch (Exception e) {
            this.getLog().error("Error parsing IP: " + e.getMessage(), e);
        }
    }

    public void save(String address) {
        snowScreen.clear();
        try {
            groupManager.save(address);
        } catch (Exception e) {
            this.getLog().error("Error creating directories: " + e.getMessage(), e);
        }
        groupManager.clear();
        //TODO: save user keys and notify if theres a change (evil server?)
        userManager.clear();
        renderer.clear();
    }

    public void load(String address) {
        try {
            groupManager.load(address);
        } catch (Exception e) {
            this.getLog().error("Error decrypting group passwords: " + e.getMessage(), e);
        }
    }
}
