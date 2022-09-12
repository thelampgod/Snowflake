package com.github.thelampgod.snowflake.util;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;

import java.sql.*;

public class DatabaseUtil {

    public static void insertUser(SocketClient client) {
        try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
            ResultSet result = conn.createStatement().executeQuery("select * from users where pubkey=\"" + client.getPubKey() + "\"");
            // if already exists then return
            if (result.next()) {
                return;
            }

            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement("INSERT INTO users(pubkey) VALUES (?)")) {
                    statement.setObject(1, client.getPubKey());

                    statement.execute();
                }
            } catch (Throwable th) {
                conn.rollback();
                throw th;
            }

            conn.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
