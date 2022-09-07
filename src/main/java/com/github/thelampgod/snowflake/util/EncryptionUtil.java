package com.github.thelampgod.snowflake.util;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.util.io.Streams;
import org.pgpainless.PGPainless;
import org.pgpainless.decryption_verification.ConsumerOptions;
import org.pgpainless.decryption_verification.DecryptionStream;
import org.pgpainless.encryption_signing.EncryptionOptions;
import org.pgpainless.encryption_signing.EncryptionStream;
import org.pgpainless.encryption_signing.ProducerOptions;
import org.pgpainless.key.info.KeyRingInfo;
import org.pgpainless.key.protection.SecretKeyRingProtector;
import org.pgpainless.key.protection.UnlockSecretKey;
import org.pgpainless.util.Passphrase;

import static net.daporkchop.lib.logging.Logging.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EncryptionUtil {

    public static String encrypt (String secret, String key) throws IOException {
        return encrypt(secret, PGPainless.readKeyRing().publicKeyRing(key));
    }

    public static String encrypt(String secret, PGPPublicKeyRing key) throws IOException {
        try {
            ByteArrayOutputStream cipher = new ByteArrayOutputStream();

            EncryptionStream s = PGPainless.encryptAndOrSign()
                    .onOutputStream(cipher)
                    .withOptions(ProducerOptions.encrypt(
                            EncryptionOptions.encryptCommunications()
                                    .addRecipient(key)
                    ));

            Streams.pipeAll(new ByteArrayInputStream(secret.getBytes()), s);
            s.close();

            return cipher.toString();
        } catch (PGPException e) {
            logger.error("Encryption failed: " + e.getMessage());
        }

        return "";
    }

    public static String decrypt(String encrypted, PGPSecretKeyRing key, String password) throws IOException {
        try {
            DecryptionStream s = PGPainless.decryptAndOrVerify()
                    .onInputStream(new ByteArrayInputStream(encrypted.getBytes()))
                    .withOptions(
                            new ConsumerOptions()
                                    .addDecryptionKey(key, SecretKeyRingProtector.unlockAnyKeyWith(Passphrase.fromPassword(password)))
                    );

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Streams.pipeAll(s, out);
            return out.toString();

        } catch (PGPException e) {
            logger.error("Decryption failed: " + e.getMessage());
        }

        return "";
    }
}
