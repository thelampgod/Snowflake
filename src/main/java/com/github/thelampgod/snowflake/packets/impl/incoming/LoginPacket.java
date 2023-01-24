package com.github.thelampgod.snowflake.packets.impl.incoming;

import com.github.thelampgod.snowflake.SocketClient;
import com.github.thelampgod.snowflake.packets.SnowflakePacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.DisconnectPacket;
import com.github.thelampgod.snowflake.packets.impl.outgoing.HandshakeStartPacket;
import com.github.thelampgod.snowflake.util.DatabaseUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.pgpainless.PGPainless;
import org.pgpainless.key.info.KeyRingInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import static com.github.thelampgod.snowflake.util.EncryptionUtil.encrypt;
import static com.github.thelampgod.snowflake.util.Helper.getConnectedClients;
import static net.daporkchop.lib.logging.Logging.logger;

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

        PGPPublicKeyRing key = PGPainless.readKeyRing().publicKeyRing(this.pubKey);
        String secret =
                RandomStringUtils.random(10, 0, 0, true, true, null, new SecureRandom());

        logger.debug("Generated `" + secret + "` as the secret password. Client needs to respond with this.");
        byte[] encryptedMessage = encrypt(secret, key);
        if (encryptedMessage.length < 1) {
            client.getConnection().sendPacket(new DisconnectPacket("Encryption fail (invalid key)", client));
            return;
        }

        client.setPubKey(this.pubKey);
        client.setSecret(secret);
        client.getConnection().sendPacket(new HandshakeStartPacket(encryptedMessage));
    }
}
