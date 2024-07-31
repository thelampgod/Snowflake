package com.github.thelampgod.snow;

import com.github.thelampgod.snow.groups.GroupManager;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.identities.IdentityManager;
import com.github.thelampgod.snow.util.Helper;
import com.github.thelampgod.snow.waypoints.render.WaypointRenderer;
import com.github.thelampgod.snow.users.UserManager;
import com.github.thelampgod.snow.waypoints.share.WaypointSharer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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
    private static ServerManager serverManager;

    public boolean xaeroLoaded = false;

    private ConfigManager configManager;
    private GroupManager groupManager;
    private UserManager userManager;
    private WaypointRenderer renderer;
    private WaypointSharer sharer;
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

        KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Snow GUI",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "Snow"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(getOrCreateSnowScreen());
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register((re) -> {
            renderer.render(re.matrixStack(), re.tickCounter().getTickDelta(false), re.camera());
        });

        try {
            Class.forName("xaero.common.minimap.radar.tracker.synced.ClientSyncedTrackedPlayerManager");
            xaeroLoaded = true;
            this.getLog().info("Xaeros Minimap found.");
        } catch (Throwable th) {
            this.getLog().error("Xaeros not loaded: " + th.getMessage(), th);
        }

        configManager = new ConfigManager();
        identityManager = new IdentityManager();
        renderer = new WaypointRenderer();
        sharer = new WaypointSharer();
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

            configManager.save();

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
            serverManager = new ServerManager(instance.configManager.getOption("lastAddress"));
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

    public WaypointSharer getSharer() {
        return sharer;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void connect(String address, String password) {
        if (serverManager != null) {
            serverManager.close();
        }
        System.out.println("Connecting to " + address);

        try {
            String[] parts = address.split(":");
            if (parts.length < 2) return;
            serverManager = new ServerManager(parts[0], Integer.parseInt(parts[1]), password);
            serverManager.connect();
            configManager.addOption("lastAddress", address);
            configManager.addOption("serverPassword", password);
        } catch (Exception e) {
            Helper.addToast("Couldn't connect to server");
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
        sharer.clear();
    }

    public void load(String address) {
        try {
            groupManager.load(address);
        } catch (Exception e) {
            this.getLog().error("Error decrypting group passwords: " + e.getMessage(), e);
        }
    }

    public String getOption(String option) {
        return configManager.getOption(option);
    }
}
