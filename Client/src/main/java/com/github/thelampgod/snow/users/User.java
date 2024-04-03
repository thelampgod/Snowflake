package com.github.thelampgod.snow.users;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static com.github.thelampgod.snow.Helper.printModMessage;

public class User {
    private final String name;
    private final int id;
    private PublicKey key;

    public User(String name, int id, PublicKey key) {
        this.name = name;
        this.id = id;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public PublicKey getKey() {
        return key;
    }

    public void setKey(String stringKey) {
        this.key = getKey(stringKey);
    }

    private PublicKey getKey(String stringKey) {
        PublicKey key = null;
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            key = factory.generatePublic(new X509EncodedKeySpec(stringKey.getBytes()));
        } catch (Exception e) {
            printModMessage("Couldn't generate public key");
            e.printStackTrace();
        }
        return key;
    }
}
