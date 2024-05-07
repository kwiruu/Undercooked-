package com.libgdx.undercooked.entities;

public abstract class Station extends Entity {
    FoodItem containedItem;
    // TODO popup (progress bar for non-source stations)
    public Station(float x, float y, int width, int height) {
        super(x, y, width, height, true);
    }
}
