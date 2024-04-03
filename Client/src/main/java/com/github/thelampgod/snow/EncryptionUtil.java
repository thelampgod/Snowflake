package com.github.thelampgod.snow;

import com.github.thelampgod.snow.groups.Group;
import com.github.thelampgod.snow.packets.WrappedPacket;
import com.github.thelampgod.snow.users.User;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

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
        System.out.println(Arrays.toString(salt));

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
        System.out.println(Arrays.toString(salt));
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

    public static byte[] toBytes(Object obj) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        return bos.toByteArray();
    }

    public static WrappedPacket toPacket(byte[] bytes) throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (WrappedPacket) ois.readObject();
    }

    public static byte[] encryptPacket(WrappedPacket packet, boolean forGroup, int id) throws Exception {
        if (forGroup) {
            return encryptPacketForGroup(packet, id);
        }

        return encryptPacketForUser(packet, id);
    }

    private static byte[] encryptPacketForUser(WrappedPacket packet, int userId) throws Exception {
        final User user = Snow.instance.getUserManager().get(userId);

        byte[] packetBytes = toBytes(packet);

        return encrypt(packetBytes, user.getKey());
    }

    public static byte[] encryptPacketForGroup(WrappedPacket packet, int groupId) throws Exception {
        final Group group = Snow.instance.getGroupManager().get(groupId);

        byte[] packetBytes = toBytes(packet);

        return encryptByPassword(packetBytes, group.getPassword());
    }
}

