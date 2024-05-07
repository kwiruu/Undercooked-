package com.libgdx.undercooked.entities;

public class FoodItem {
    // e.g. Chopping Type 1 (Tomato) changes it into Type 2 (Chopped Tomatoes)
    // e.g. Stew(ing) Type 2 (Chopped Tomatoes) changes it into Type 3 (Tomato Soup)
    FoodType fType;

    public FoodItem(FoodType ft) {
        fType = ft;
    }

}
