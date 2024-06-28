package com.github.thelampgod.snow.identities;

import com.github.thelampgod.snow.util.EncryptionUtil;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class Identity {
    private final String name;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    public Identity(String name) throws NoSuchAlgorithmException {
        this.name = name;
        KeyPair pair = generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
    }

    public Identity(String name, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.name = name;
        this.privateKey = EncryptionUtil.parseEncoded(key);
        this.publicKey = EncryptionUtil.getPublicKeyFromPrivate(privateKey);
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getName() {
        return name;
    }
}
