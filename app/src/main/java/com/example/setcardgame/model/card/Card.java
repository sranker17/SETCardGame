package com.example.setcardgame.model.card;

import androidx.annotation.NonNull;

public class Card {
    private final Color color;
    private final Shape shape;
    private final Quantity quantity;

    public Card(Color color, Shape shape, Quantity quantity) {
        this.color = color;
        this.shape = shape;
        this.quantity = quantity;
    }

    public Card(String color, String shape, String quantity) {
        this.color = Color.getColorFromString(color);
        this.shape = Shape.getShapeFromString(shape);
        this.quantity = Quantity.getQuantityFromString(quantity);
    }

    public Color getColor() {
        return color;
    }

    public Shape getShape() {
        return shape;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    @NonNull
    public String toString() {
        return color.label + shape.label + quantity.label;
    }
}
