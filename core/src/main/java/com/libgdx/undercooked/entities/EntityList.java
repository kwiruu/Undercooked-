package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EntityList {
    private final SpriteBatch batch;
    private final Array<Station> stationArray;
    private final TiledMap map;

    public EntityList(TiledMap map, SpriteBatch batch) {
        this.map = map;
        this.batch = batch;
        stationArray = new Array<>();
        renderEntity(map);
    }
    public void render() {

        // call each object added to the array of different stations!
        // array stoves, chopping_boards, rice_cookers, food_sources!
        for (Station stove : stationArray) {
            stove.render();
        }
    }

    private void renderEntity(TiledMap map) {
        float x, y, width, height;
        MapLayer objectLayer = map.getLayers().get("station_layer");

        // kani kay for adding the stations to an array of different stations!
        // array stoves, chopping_boards, rice_cookers, food_sources!
        // it is called in the constructor then to be used in the mapManager

        for (MapObject object : objectLayer.getObjects()) {
            // if the object is named stove in the tiled map then mu create siyag new nga stove!
            if (object.getName().equals("stove")) {
                 x = object.getProperties().get("x", Float.class);
                 y = object.getProperties().get("y", Float.class) + 55;
                 width = object.getProperties().get("width", Float.class);
                 height = object.getProperties().get("height", Float.class);

                Stove stove = new Stove(x, y, (int) width, (int) height, batch);
                stationArray.add(stove); // Add stove to the array
            }
            if (object.getName().equals("chopping_board")) {
                 x = object.getProperties().get("x", Float.class)+70;
                 y = object.getProperties().get("y", Float.class)+25;
                 width = object.getProperties().get("width", Float.class);
                 height = object.getProperties().get("height", Float.class);

                ChoppingBoard choppingBoard = new ChoppingBoard(x, y, (int) width, (int) height, batch);
                stationArray.add(choppingBoard); // Add stove to the array
            }
            if (object.getName().equals("rice_cooker")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+40;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                RiceCooker riceCooker = new RiceCooker(x, y, (int) width, (int) height, batch);
                stationArray.add(riceCooker); // Add stove to the array
            }

            //--------------------IMPORTANT-----------------------//

            // for the foodsources!!!
            // in the tiled map, we only need to name the foodsources like these!
            // more modular map creation!!!!!

            if (object.getName().equals("onion_source")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource onion = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.onion);
                stationArray.add(onion); // Add onion to the array
            }else if (object.getName().equals("meat_source")) {
                x = object.getProperties().get("x", Float.class)+16;
                y = object.getProperties().get("y", Float.class)+40;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource meat = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.meat);
                stationArray.add(meat); // Add meat to the array
            } else if (object.getName().equals("fish_tank_source")) {
                x = object.getProperties().get("x", Float.class)+32;
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource fish = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.fish);
                stationArray.add(fish); // Add stove to the array
            } else if (object.getName().equals("tomato_source")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource tomato = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.tomato);
                stationArray.add(tomato); // Add tomato to the array
            } else if (object.getName().equals("pickle_source")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource pickle = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.pickle);
                stationArray.add(pickle); // Add pickle to the array
            }
        }
    }

    public Station pointStation (Vector2 v2) {
        Rectangle r;
        float displacement = 48F;
        for (Station s: stationArray) {
            System.out.println(s + " checking (" + s.getX() + "-" + (s.getX()+displacement) + ", " + s.getY() + "-" + (s.getY()+displacement) + ")");
            // needs tweaking
            r = new Rectangle(s.getX(), s.getY(), 32f, 64f);
            if (r.contains(v2)) {
                return s;
            }
        }
        return null;
    }
}
