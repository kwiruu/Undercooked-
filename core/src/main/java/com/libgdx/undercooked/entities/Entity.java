package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class Entity {

    private Array<Stove> stoves;
    private SpriteBatch batch;

    public Entity(TiledMap map, SpriteBatch batch) {
        this.batch = batch;
        stoves = new Array<>();
        populateStoves(map);
    }

    public Array<Stove> getStoves() {
        return stoves;
    }

    public void render() {
        for (Stove stove : stoves) {
            stove.render();
        }
    }

    private void populateStoves(TiledMap map) {
        MapLayer objectLayer = map.getLayers().get("station_layer");
        // Get the different stations
        for (MapObject object : objectLayer.getObjects()) {
            if (object.getName().equals("stove")) {
                float stovex = object.getProperties().get("x", Float.class);
                float stovey = object.getProperties().get("y", Float.class) + 55;
                float stoveWidth = object.getProperties().get("width", Float.class);
                float stoveHeight = object.getProperties().get("height", Float.class);

                Stove stove = new Stove(stovex, stovey, (int) stoveWidth, (int) stoveHeight, batch);
                stoves.add(stove); // Add stove to the array
            }
        }
    }
}
