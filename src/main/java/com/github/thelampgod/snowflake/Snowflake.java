package com.github.thelampgod.snowflake;

import net.daporkchop.lib.logging.LogAmount;

import static net.daporkchop.lib.logging.Logging.*;

public class Snowflake {
    public static Snowflake INSTANCE;
    private static final int PORT = 2147;

    private final SnowflakeServer server;

    public Snowflake() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }

        logger.setLogAmount(LogAmount.DEBUG);
        logger.info("Starting Snowflake server on port " + PORT);
        this.server = new SnowflakeServer();
        logger.info("Listening...");
        server.start(PORT);
    }

    public SnowflakeServer getServer() {
        return this.server;
    }

}