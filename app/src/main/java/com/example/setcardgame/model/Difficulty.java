package com.example.setcardgame.model;

import androidx.annotation.NonNull;

public enum Difficulty {
    EASY("Easy"),
    NORMAL("Normal");

    public final String label;

    Difficulty(String label) {
        this.label = label;
    }

    public static Difficulty getDifficultyFromString(String label) {
        if (label.equals(EASY.toString())) {
            return EASY;
        }
        return NORMAL;
    }

    @NonNull
    @Override
    public String toString() {
        return label;
    }
}
