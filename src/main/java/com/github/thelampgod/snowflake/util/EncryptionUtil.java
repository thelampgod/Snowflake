package com.github.thelampgod.snowflake.util;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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

    public static String asciiArmored(PublicKey pub) {
        // Get the encoded bytes of the public key
        byte[] encodedKey = pub.getEncoded();

        // Encode the bytes using Base64
        String base64Encoded = Base64.getEncoder().encodeToString(encodedKey);

        // Wrap the Base64-encoded string in ASCII armor format
        StringBuilder asciiArmoredKey = new StringBuilder();
        asciiArmoredKey.append("-----BEGIN PUBLIC KEY-----\n");

        // Insert line breaks every 64 characters
        for (int i = 0; i < base64Encoded.length(); i += 64) {
            asciiArmoredKey.append(base64Encoded.substring(i, Math.min(i + 64, base64Encoded.length())));
            asciiArmoredKey.append("\n");
        }

        asciiArmoredKey.append("-----END PUBLIC KEY-----");

        return asciiArmoredKey.toString();
    }

    public static PublicKey parseAsciiArmored(String asciiArmoredKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Remove the header and footer
        String base64Encoded = asciiArmoredKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Remove any white spaces

        // Decode the Base64 string
        byte[] decodedKey = Base64.getDecoder().decode(base64Encoded);

        // Generate a PublicKey object from the decoded bytes
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Change "RSA" to the algorithm used for your key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }
}
