package com.example.setcardgame.model.card;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Card {
    private final Color color;
    private final Shape shape;
    private final Quantity quantity;

    public Card(String color, String shape, String quantity) {
        this.color = Color.getColorFromString(color);
        this.shape = Shape.getShapeFromString(shape);
        this.quantity = Quantity.getQuantityFromString(quantity);
    }

    @NonNull
    public String toString() {
        return color.label + shape.label + quantity.label;
    }
}
