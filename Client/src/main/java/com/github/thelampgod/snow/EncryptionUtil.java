package com.github.thelampgod.snow;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptionUtil {

    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptForSender(byte[] data, int sender) {
        return new byte[0];
    }

    public static byte[] encryptForSender(byte[] data, int sender) {
        return new byte[0];
    }
}

