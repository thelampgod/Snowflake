package com.github.thelampgod.snowflake.util;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SnowflakeServer;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.database.Database;

import java.util.Set;

public class Helper {

    public static Set<SocketClient> getConnectedClients() {
        return getServer().connectedClients;
    }

    public static SnowflakeServer getServer() {
        return Snowflake.INSTANCE.getServer();
    }

    public static Database getDb() {
        return Snowflake.INSTANCE.getDb();
    }
}
