package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.PlainMessagePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.RecipientsPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Set;

public class ListRecipientsPacket extends SnowflakePacket {
    public ListRecipientsPacket(SocketClient sender) {
        super(sender);
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        super.handle();
        final SocketClient client = this.getSender();
        Set<Integer> recipients = client.getConnection().recipientsIds;
        if (recipients.isEmpty()) {
            client.getConnection().sendPacket(new PlainMessagePacket("No recipients"));
            return;
        }

        client.getConnection().sendPacket(new RecipientsPacket(recipients));
    }
}
