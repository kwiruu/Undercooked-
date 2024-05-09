package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;

import java.awt.*;

public abstract class Station {
    //temporarily stopped extends Entity
    FoodItem containedItem;
    private float x;
    private float y;
    private int width;
    private int height;
    // TODO popup (progress bar for non-source stations)

    public Station(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
