package com.github.thelampgod.snow;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

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



    public static byte[] encryptByPassword(byte[] data, byte[] passwordBytes) throws Exception {
        // Generate a 128-bit key from the password
        SecretKeySpec key = new SecretKeySpec(passwordBytes, "AES");

        // Generate a random initialization vector
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Encrypt the data
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] ciphertext = cipher.doFinal(data);

        // Combine IV and ciphertext
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

        return combined;
    }

    public static byte[] decryptByPassword(byte[] data, byte[] passwordBytes) throws Exception {
        // Generate a 128-bit key from the password
        SecretKeySpec key = new SecretKeySpec(passwordBytes, "AES");

        // Extract IV from the combined data
        byte[] iv = new byte[16];
        System.arraycopy(data, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Decrypt the data
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] decryptedData = cipher.doFinal(data, iv.length, data.length - iv.length);

        return decryptedData;
    }
}

