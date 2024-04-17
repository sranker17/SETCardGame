package com.example.setcardgame.model.card;

public enum Quantity {
    ONE("1"),
    TWO("2"),
    THREE("3");

    public final String label;

    Quantity(String label) {
        this.label = label;
    }

    public static Quantity getQuantityFromString(String quantity) {
        if (quantity.equals("ONE")) {
            return Quantity.ONE;
        } else if (quantity.equals("TWO")) {
            return Quantity.TWO;
        }

        return Quantity.THREE;
    }
}
