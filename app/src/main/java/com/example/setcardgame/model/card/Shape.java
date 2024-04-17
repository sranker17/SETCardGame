package com.example.setcardgame.model.card;

public enum Shape {
    DIAMOND("d"),
    WAVY("s"),
    OVAL("p");

    public final String label;

    Shape(String label) {
        this.label = label;
    }

    public static Shape getShapeFromString(String shape) {
        if (shape.equals("DIAMOND")) {
            return Shape.DIAMOND;
        } else if (shape.equals("WAVY")) {
            return Shape.WAVY;
        }

        return Shape.OVAL;
    }
}
