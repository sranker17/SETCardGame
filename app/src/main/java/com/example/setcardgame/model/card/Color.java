package com.example.setcardgame.model.card;

public enum Color {
    GREEN("g"),
    RED("r"),
    PURPLE("p");

    public final String label;

    Color(String label) {
        this.label = label;
    }

    public static Color getColorFromString(String color) {
        if (color.equals("GREEN")) {
            return Color.GREEN;
        } else if (color.equals("RED")) {
            return Color.RED;
        }

        return Color.PURPLE;
    }
}
