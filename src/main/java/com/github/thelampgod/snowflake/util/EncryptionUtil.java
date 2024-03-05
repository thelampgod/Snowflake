package com.github.thelampgod.snowflake.util;


import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

import static com.github.thelampgod.snowflake.util.Helper.getLog;

public class EncryptionUtil {

    public static byte[] encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            getLog().info("Failed to encrypt");
        }

        return new byte[0];
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            getLog().info("Failed to decrypt");
        }

        return new byte[0];
    }
}
