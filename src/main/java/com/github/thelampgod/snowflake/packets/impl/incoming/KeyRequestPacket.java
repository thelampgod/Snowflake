package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.ClientHandler;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.KeyResponsePacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.thelampgod.snowflake.util.Helper.getDb;

public class KeyRequestPacket extends SnowflakePacket {
    private final int id;
    public KeyRequestPacket(DataInputStream in, ClientHandler sender) throws IOException {
        super(sender);
        this.id = in.readInt();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        if (!super.isAuthenticated()) {
            return;
        }
        if (id < 1) return;
        String key = "";
        try (Connection conn = getDb().getConnection()) {
            ResultSet result = DatabaseUtil.runQuery("select pubkey from users where id=" + id, conn).getResultSet();
            while (result.next()) {
                key = result.getString("pubkey");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!key.isEmpty()) {
            this.getSender().sendPacket(new KeyResponsePacket(id, key));
        } else {
            // write invalid
            this.getSender().sendPacket(new KeyResponsePacket(-1, ""));
        }
    }
}
