package com.github.thelampgod.snowflake.util;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.database.Database;
import com.github.thelampgod.snowflake.groups.Group;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.sql.*;
import java.util.*;

public class DatabaseUtil {

    public static int insertUser(SocketClient client) {
        try (Connection conn = Snowflake.INSTANCE.getDb().getConnection()) {
            // if already exists then return the id
            try (PreparedStatement statement = conn.prepareStatement("select id from users where pubkey=?")) {
                statement.setString(1, client.getPubKey());

                statement.execute();
                ResultSet result = statement.getResultSet();

                if (result.next()) {
                    return result.getInt("id");
                }
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

    public static int insertGroup(Group group, Database db) {
        int insertedId = -1;
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement(
                        "insert into groups(name, owner_id) values(?, ?)")) {
                    statement.setObject(1, group.getName());
                    statement.setInt(2, group.getOwnerId());

                    statement.execute();
                }
                ResultSet r = runQuery("select last_insert_rowid() AS last_id", conn).getResultSet();
                insertedId = r.getInt("last_id");

            } catch (Throwable th) {
                conn.rollback();
                throw th;
            }
            conn.commit();
            return insertedId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insertedId;
    }

    public static Collection<? extends Group> getGroupsFromDb(Database db) {
        Map<Integer, Group> temp = Maps.newHashMap();

        try (Connection conn = db.getConnection()) {
            ResultSet result = DatabaseUtil.runQuery(
                    "select id, name, owner_id, user_id from groups join group_users on id=group_id", conn).getResultSet();
            while (result.next()) {
                int groupId = result.getInt("id");
                String name = result.getString("name");
                int owner = result.getInt("owner_id");
                Group group = temp.computeIfAbsent(groupId, id -> new Group(
                        name,
                        id,
                        owner));

                group.addUser(result.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return temp.values();
    }

    public static void removeGroup(Group group, Database db) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement("delete from groups where id=?")) {
                    statement.setInt(1, group.getId());
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

    public static void addUserToGroup(int clientId, Group group, Database db) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement("insert into group_users(user_id, group_id) values(?,?)")) {
                    statement.setInt(1, clientId);
                    statement.setInt(2, group.getId());
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

    public static void removeUserFromGroup(int clientId, Group group, Database db) {
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statement = conn.prepareStatement("delete from group_users where user_id=? and group_id=?")) {
                    statement.setInt(1, clientId);
                    statement.setInt(2, group.getId());
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
}
