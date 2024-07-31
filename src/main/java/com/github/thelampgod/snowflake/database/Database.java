package com.github.thelampgod.snowflake.database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class Database {

    private final BasicDataSource database;

    public Database(String url) {
        this.database = connect(url);
        database.setConnectionInitSqls(List.of(
                "PRAGMA foreign_keys = ON;"
        ));
    }

    private BasicDataSource connect(String url) {
        final BasicDataSource db = new BasicDataSource();
        db.setDriverClassName("org.sqlite.JDBC");
        db.setUrl(url);
        db.setInitialSize(1);

        getLog().info("Connected to database.");
        return db;
    }

    public Connection getConnection() throws SQLException {
        return database.getConnection();
    }
}