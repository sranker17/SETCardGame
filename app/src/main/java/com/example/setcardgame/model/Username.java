package com.example.setcardgame.model;

public class Username {
    private static String name;

    private Username() {
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Username.name = name;
    }
}
