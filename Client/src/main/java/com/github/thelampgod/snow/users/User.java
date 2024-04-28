package com.github.thelampgod.snow.users;

import com.github.thelampgod.snow.EncryptionUtil;

import java.security.PublicKey;

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

    private PublicKey getKey(String asciiArmored) {
        PublicKey key = null;
        try {
            key = EncryptionUtil.parseAsciiArmored(asciiArmored);
            printModMessage("Succesffully parsed " + this.name + "'s pubkey :D");
            System.out.println(asciiArmored);
        } catch (Exception e) {
            printModMessage("Couldn't generate public key");
            e.printStackTrace();
        }
        return key;
    }
}
