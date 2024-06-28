package com.github.thelampgod.snow.util;

import com.github.thelampgod.snow.Snow;
import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.WrappedPacket;
import com.github.thelampgod.snow.users.User;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.*;
import java.util.Base64;

public class EncryptionUtil {

    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 128; // 128 bits for AES
    private static final int SALT_LENGTH = 16; // Salt length in bytes

    public static byte[] encryptByPassword(byte[] data, byte[] passwordBytes) throws Exception {
        char[] password = bytesToChars(passwordBytes);
        byte[] salt = new byte[SALT_LENGTH]; // Generate a random salt
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        // Generate a key from the password and salt
        SecretKey key = generateKeyFromPassword(password, salt);

        byte[] enc = encrypt(data, key);
        return concatenateArrays(salt, enc);
    }

    private static byte[] concatenateArrays(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    public static byte[] decryptByPassword(byte[] encryptedData, byte[] passwordBytes) throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(encryptedData, 0, salt, 0, SALT_LENGTH);

        byte[] encryptedDataWithoutSalt = new byte[encryptedData.length - SALT_LENGTH];
        System.arraycopy(encryptedData, SALT_LENGTH, encryptedDataWithoutSalt, 0, encryptedData.length - SALT_LENGTH);

        char[] password = bytesToChars(passwordBytes);
        SecretKey key = generateKeyFromPassword(password, salt);
        return decrypt(encryptedDataWithoutSalt, key);
    }

    public static SecretKey generateKeyFromPassword(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static char[] bytesToChars(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8).toCharArray();
    }

    public static WrappedPacket toPacket(byte[] bytes) {
        return WrappedPacket.fromBytes(bytes);
    }

    public static byte[] encryptPacket(WrappedPacket packet, boolean forGroup, int id) throws Exception {
        if (forGroup) {
            return encryptPacketForGroup(packet, id);
        }

        return encryptPacketForUser(packet, id);
    }

    private static byte[] encryptPacketForUser(WrappedPacket packet, int userId) throws Exception {
        final User user = Snow.instance.getUserManager().get(userId);

        byte[] packetBytes = packet.data();

        return encrypt(packetBytes, user.getKey());
    }

    public static byte[] encryptPacketForGroup(WrappedPacket packet, int groupId) throws Exception {
        final Group group = Snow.instance.getGroupManager().get(groupId);

        byte[] packetBytes = packet.data();

        return encryptByPassword(packetBytes, group.getPassword());
    }

    public static String asciiArmored(PublicKey pub) {
        // Get the encoded bytes of the public key
        byte[] encodedKey = pub.getEncoded();

        // Encode the bytes using Base64
        String base64Encoded = base64Encode(encodedKey);

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
        byte[] decodedKey = base64Decode(base64Encoded);

        // Generate a PublicKey object from the decoded bytes
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Change "RSA" to the algorithm used for your key
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        return keyFactory.generatePublic(keySpec);
    }

    public static String base64Encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] base64Decode(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }

    public static byte[] generatePassword() {
        final String generated = RandomStringUtils.random(16, 0, 0, true, true, null, new SecureRandom());
        System.out.println("generated " + generated);
        return generated.getBytes();
    }

    public static PrivateKey generateRsaPrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        return pair.getPrivate();
    }

    public static PrivateKey parseEncoded(byte[] encodedKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey getPublicKeyFromPrivate(PrivateKey privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPrivateCrtKey key = (RSAPrivateCrtKey)privateKey;

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }
}

