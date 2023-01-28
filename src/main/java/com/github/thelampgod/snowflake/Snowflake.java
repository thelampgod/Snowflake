package com.github.thelampgod.snowflake;

import com.github.thelampgod.snowflake.database.Database;
import net.daporkchop.lib.logging.LogAmount;

import java.io.IOException;
import java.net.URL;

import com.google.common.io.Resources;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static net.daporkchop.lib.logging.Logging.*;

public class Snowflake {
    public static Snowflake INSTANCE;
    private static final int PORT = 2147;

    private final SnowflakeServer server;
    private final Database database;

    public Snowflake(String[] args) {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        if (args != null && args.length > 0 && args[0].equals("--debug")) {
            logger.setLogAmount(LogAmount.DEBUG);
        }

        try {
            logger.info("Connecting to database...");
            this.database = new Database("jdbc:sqlite:snowflake.db");
            applySchema();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Starting Snowflake server on port " + PORT);
        this.server = new SnowflakeServer();

        final Thread terminalConsoleHandler = new Thread(() -> new TerminalConsole(this).start(), "Terminal console handler");
        terminalConsoleHandler.setDaemon(true);
        terminalConsoleHandler.start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "shutdown_thread"));

        logger.info("Listening...");
        server.start(PORT);
    }

    private void shutdown() {
        logger.info("Shutting down");
        server.stop();
        try {
            database.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void applySchema() throws IOException, SQLException {
        final URL resource = this.getClass().getClassLoader().getResource("schema.sql");

        if (resource == null) {
            throw new RuntimeException("failed to read schema");
        }
        String schema = Resources.toString(resource, StandardCharsets.UTF_8);

        try (Connection conn = this.database.getConnection()) {
            conn.setAutoCommit(false);
            for (String sql : schema.split(";")) {
                try (final Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }

            conn.commit();
        }
    }

    public SnowflakeServer getServer() {
        return this.server;
    }

    public Database getDb() {
        return this.database;
    }
}