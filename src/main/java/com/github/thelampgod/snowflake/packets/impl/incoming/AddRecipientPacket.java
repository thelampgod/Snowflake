package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;
import static com.github.thelampgod.snowflake.util.Helper.getDb;

public class AddRecipientPacket extends SnowflakePacket {
    private int id = 0;
    private String key = "";

    public AddRecipientPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        if (in.readByte() == 0) {
            this.id = in.readInt();
        } else {
            this.key = in.readUTF();
        }
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        if (key.isEmpty()) {
            if (addViaId(id)) {
                this.getSender().getConnection().sendPacket(new PlainMessagePacket("Added recipient successfully"));
            } else {
                this.getSender().getConnection().sendPacket(new PlainMessagePacket("Failed adding recipient"));
            }
            return;
        }

        if (addViaKey(key)) {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("Added recipient successfully"));
        } else {
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("Failed adding recipient"));
        }

    }

    private boolean addViaId(int id) {
        //check connected clients first
        Optional<SocketClient> optRecipient = getConnectedClients().stream()
                .filter(c -> c.getId() == id)
                .findAny();

        if (optRecipient.isPresent()) {
            SocketClient recipient = optRecipient.get();
            DatabaseUtil.insertRecipient(recipient.getId(), recipient.getPubKey(), this.getSender().getId());
            this.getSender().getConnection().recipientsIds.add(recipient.getId());
            return true;
        }
        //not connected, check database
        try (Connection conn = getDb().getConnection()) {
            ResultSet result =
                    DatabaseUtil.runQuery(
                            "select pubkey from users where id=\"" + id + "\"", conn).getResultSet();
            if (result.next()) {
                String pubKey = result.getString("pubkey");
                result.close();

                DatabaseUtil.insertRecipient(id, pubKey, this.getSender().getId());
                this.getSender().getConnection().recipientsIds.add(id);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean addViaKey(String recipientKey) {
        //check connected clients first
        Optional<SocketClient> optRecipient = getConnectedClients().stream()
                .filter(c -> c.getPubKey().equals(recipientKey))
                .findAny();

        if (optRecipient.isPresent()) {
            SocketClient recipient = optRecipient.get();
            DatabaseUtil.insertRecipient(recipient.getId(), recipient.getPubKey(), this.getSender().getId());
            this.getSender().getConnection().recipientsIds.add(recipient.getId());
            return true;
        }

        //not connected, check database
        try (Connection conn = getDb().getConnection()) {
            ResultSet result =
                    DatabaseUtil.runQuery(
                            "select id from users where pubkey=\"" + recipientKey + "\"", conn).getResultSet();
            if (result.next()) {
                int userId = result.getInt("id");
                result.close();

                DatabaseUtil.insertRecipient(userId, recipientKey, this.getSender().getId());
                this.getSender().getConnection().recipientsIds.add(userId);
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}
