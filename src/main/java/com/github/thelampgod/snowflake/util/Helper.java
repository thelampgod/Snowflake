package com.github.thelampgod.snowflake.util;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SnowflakeServer;
import com.github.thelampgod.snowflake.database.Database;
import org.slf4j.Logger;

public class Helper {

    public static SnowflakeServer getServer() {
        return Snowflake.INSTANCE.getServer();
    }

    public static Database getDb() {
        return Snowflake.INSTANCE.getDb();
    }

    public static Logger getLog() {
        return Snowflake.INSTANCE.logger;
    }
}
