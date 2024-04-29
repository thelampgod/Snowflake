package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.Snowflake;
import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.DisconnectPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.HandshakeStartPacket;
import com.github.thelampgod.snowflake.util.EncryptionUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;

import static com.github.thelampgod.snowflake.util.EncryptionUtil.encrypt;
import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class LoginPacket extends SnowflakePacket {
    private final String pubKey;

    public LoginPacket(DataInputStream in, SocketClient sender) throws IOException {
        super(sender);
        this.pubKey = in.readUTF();
    }

    @Override
    public void writeData(DataOutputStream out) throws IOException {

    }

    @Override
    public void handle() throws IOException {
        final SocketClient client = this.getSender();

        //dont login twice
        if (client.isAuthenticated()) return;

        PublicKey key;
        try {
            key = EncryptionUtil.parseAsciiArmored(pubKey);
        } catch (Exception e) {
            getLog().info("Couldn't generate public key");
            client.getConnection().sendPacket(new DisconnectPacket("Bad key", client));
            e.printStackTrace();
            return;
        }

        String secret =
                RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());

        getLog().debug("Generated `" + secret + "` as the secret password. Client needs to respond with this.");
        byte[] encryptedMessage = encrypt(secret.getBytes(), key);
        if (encryptedMessage.length < 1) {
            client.getConnection().sendPacket(new DisconnectPacket("Encryption fail (invalid key)", client));
            return;
        }

        client.setPubKey(this.pubKey);
        client.setSecret(secret);

        SocketClient receiver = Snowflake.INSTANCE.getServer().getClientReceiver(client.getLinkString());
        receiver.getConnection().sendPacket(new HandshakeStartPacket(encryptedMessage));
    }
}
