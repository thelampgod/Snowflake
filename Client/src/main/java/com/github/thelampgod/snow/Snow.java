package com.github.thelampgod.snow;

import com.github.thelampgod.snow.commands.impl.AuthenticateCommand;
import com.github.thelampgod.snow.groups.GroupManager;
import com.github.thelampgod.snow.render.WaypointRenderer;
import com.github.thelampgod.snow.users.UserManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
