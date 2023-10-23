package com.example.setcardgame.Model;

public class Username {
    private static String username;

    private Username() {
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Username.username = username;
    }
}
