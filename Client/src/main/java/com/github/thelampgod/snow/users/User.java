package com.github.thelampgod.snow.users;

public class User {
    private final String name;
    private final int id;
    private String key;

    public User(String name, int id, String key) {
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
