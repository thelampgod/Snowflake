package com.github.thelampgod.snow;

import com.github.thelampgod.snow.commands.impl.AuthenticateCommand;
import com.github.thelampgod.snow.groups.GroupManager;
import com.github.thelampgod.snow.gui.SnowScreen;
import com.github.thelampgod.snow.render.WaypointRenderer;
import com.github.thelampgod.snow.users.UserManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class Snow implements ModInitializer {
    public static final Snow instance = new Snow();
    private final Logger LOGGER = LogManager.getLogger("Snow");

    private static final String IP = "127.0.0.1";
    private static final int PORT = 2147;
    private static ServerManager serverManager;

    private GroupManager groupManager;
    private UserManager userManager;

    private WaypointRenderer renderer;
    @Override
    public void onInitialize() {
        LOGGER.info("Loading...");
        long now = System.currentTimeMillis();
        init();
        LOGGER.info("Loaded in {}ms", System.currentTimeMillis() - now);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "shutdown_thread"));
    }

    private void init() {
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
                client.setScreen(new SnowScreen(Text.literal("Snow")));
            }
        });

        renderer = new WaypointRenderer();
        groupManager = new GroupManager();
        userManager = new UserManager();
        serverManager = new ServerManager(IP, PORT);
    }

    private void shutdown() {
        LOGGER.info("Shutting down...");
        long now = System.currentTimeMillis();
        try {
            serverManager.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        if (renderer == null) {
            renderer = new WaypointRenderer();
        }
        return renderer;
    }
}
