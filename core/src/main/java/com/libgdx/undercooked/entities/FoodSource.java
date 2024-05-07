package com.libgdx.undercooked.entities;

public class FoodSource extends Station {
    public FoodSource(float x, float y, int width, int height, boolean isStatic, FoodType foodType) {
        super(x, y, width, height, isStatic);
        containedItem = new FoodItem(foodType);
    }
    // TODO station to continue
    // when containedItem is taken, generate a new one
}
