package com.example.setcardgame.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Username {
    @Getter
    private static String name;

    public static void setName(String name) {
        Username.name = name;
    }
}