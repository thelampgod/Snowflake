package com.github.thelampgod.snowflake.database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import static net.daporkchop.lib.logging.Logging.*;

public class Database {

    private final BasicDataSource database;

    public Database(String url) {
        this.database = connect(url);
    }

    private BasicDataSource connect(String url) {
        final BasicDataSource db = new BasicDataSource();
        db.setDriverClassName("org.sqlite.JDBC");
        db.setUrl(url);
        db.setInitialSize(1);

        logger.info("Connected to database.");
        return db;
    }

    public Connection getConnection() throws SQLException {
        return database.getConnection();
    }
}