package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

public class RemoveRecipientPacket extends SnowflakePacket {
    private int id = 0;
    private String key = "";

    public RemoveRecipientPacket(DataInputStream in, SocketClient sender) throws IOException {
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
        final SocketClient client = this.getSender();
        if (key.isEmpty()) {
            DatabaseUtil.removeRecipient(id, client.getId());
            client.getConnection().recipientsIds.remove(id);

            this.getSender().getConnection().sendPacket(new PlainMessagePacket("Removed recipient successfully"));
            return;
        }

        Optional<Integer> id = DatabaseUtil.removeRecipient(key, client.getId());

        if (id.isPresent()) {
            client.getConnection().recipientsIds.remove(id.get());
            this.getSender().getConnection().sendPacket(new PlainMessagePacket("Removed recipient successfully"));
        }
    }
}
