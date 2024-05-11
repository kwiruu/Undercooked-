package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public abstract class Station {
    //temporarily stopped extends Entity
    FoodItem containedItem;
    private float x;
    private float y;
    private int width;
    private int height;
    protected TextureAtlas floating_iconAtlas;
    protected float frameDuration;
    protected float stateTime; // Time elapsed since the start of the animation
    protected Array<TextureAtlas.AtlasRegion> floatingIconFrames;

    // TODO popup (progress bar for non-source stations)

    public Station(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        Array<TextureAtlas.AtlasRegion> floatingIconFrames;
        floating_iconAtlas = new TextureAtlas("assets/floating_icons/float_icons.atlas");
        frameDuration = 1f; // Set the duration of each frame (adjust as needed)
        stateTime = 0f; // Initialize stateTime
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
