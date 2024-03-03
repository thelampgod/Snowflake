package com.github.thelampgod.snow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Snow implements ModInitializer {
    public static final Snow instance = new Snow();
    private final Logger LOGGER = LogManager.getLogger("Snow");
    @Override
    public void onInitialize() {
        LOGGER.info("Loading...");
        long now = System.currentTimeMillis();
        init();
        LOGGER.info("Loaded in {}ms", System.currentTimeMillis() - now);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "shutdown_thread"));
    }

    private void init() {
    }

    private void shutdown() {
        LOGGER.info("Shutting down...");
        long now = System.currentTimeMillis();
        //shut down
        LOGGER.info("Shutdown in {}ms", System.currentTimeMillis() - now);
    }
}
