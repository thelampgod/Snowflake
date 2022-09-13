package com.github.thelampgod.snowflake.util;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;

import java.sql.*;

public class DatabaseUtil {

    public static int insertUser(SocketClient client) {
        try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
            // if already exists then return the id
            ResultSet result = runQuery("select id from users where pubkey=\"" + client.getPubKey() + "\"", conn).getResultSet();
            if (result.next()) {
                return result.getInt("id");
            }

            int insertedId;
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement("INSERT INTO users(pubkey) VALUES(?)")) {
                    statement.setObject(1, client.getPubKey());

                    statement.execute();
                }
                // get inserted id
                ResultSet r = runQuery("select last_insert_rowid() AS last_id", conn).getResultSet();
                insertedId = r.getInt("last_id");


            } catch (Throwable th) {
                conn.rollback();
                throw th;
            }

            conn.commit();

            return insertedId;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Statement runQuery(String query, Connection conn) throws SQLException {
        Statement s = conn.createStatement();
        s.execute(query);
        return s;
    }
}
