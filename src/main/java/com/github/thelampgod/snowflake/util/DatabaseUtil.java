package com.github.thelampgod.snowflake.util;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;

import java.sql.*;
import java.util.Optional;

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

    public static void insertRecipient(int recipientId, String recipientKey, int userId) {
        try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {

            ResultSet result =
                    runQuery("select * from recipients where user_id=" + userId + " and recipient_user_id=" + recipientId, conn).getResultSet();
            if (result.next()) {
                //recipient already exists
                result.close();
                return;
            }

            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement(
                        "insert into recipients(recipient_user_id, pubkey, user_id) values(?, ?, ?)")) {
                    statement.setInt(1, recipientId);
                    statement.setObject(2, recipientKey);
                    statement.setInt(3, userId);

                    statement.execute();
                }

            } catch (Throwable th) {
                conn.rollback();
                throw th;
            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeRecipient(int recipientId, int userId) {
        try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement(
                        "delete from recipients where `recipient_user_id`=? AND `user_id`=?")) {
                    statement.setInt(1, recipientId);
                    statement.setInt(2, userId);

                    statement.execute();
                }

            } catch (Throwable th) {
                conn.rollback();
                throw th;
            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Optional<Integer> removeRecipient(String recipientKey, int userId) {
        Optional<Integer> id = Optional.empty();
        try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
            ResultSet result = runQuery("select recipient_user_id from recipients where `pubkey`=\"" + recipientKey + "\"", conn).getResultSet();
            if (result.next()) {
                id = Optional.of(result.getInt("id"));
            }

            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement(
                        "delete from recipients where `pubkey`=? AND `user_id`=?")) {
                    statement.setObject(1, recipientKey);
                    statement.setInt(2, userId);

                    statement.execute();
                }

            } catch (Throwable th) {
                conn.rollback();
                throw th;
            }
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}
