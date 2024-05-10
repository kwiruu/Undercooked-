package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class Entity {
    private final SpriteBatch batch;
    private final Array<Stove> stoves;
    private final Array<ChoppingBoard> chopping_boards;
    private final Array<RiceCooker> rice_cookers;
    private final Array<FoodSource> food_sources;
    float x,y,width,height;

    public Entity(TiledMap map, SpriteBatch batch) {
        this.batch = batch;
        stoves = new Array<>();
        chopping_boards = new Array<>();
        rice_cookers = new Array<>();
        food_sources = new Array<>();

        renderEntity(map);
    }
    public void render() {

        // call each object added to the array of different stations!
        // array stoves, chopping_boards, rice_cookers, food_sources!
        for (Stove stove : stoves) {
            stove.render();
        }
        for (ChoppingBoard choppingBoard : chopping_boards) {
            choppingBoard.render();
        }
        for (RiceCooker riceCooker : rice_cookers) {
            riceCooker.render();
        }
        for(FoodSource foodSource: food_sources){
            foodSource.render();
        }
    }

    private void renderEntity(TiledMap map) {
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
                stoves.add(stove); // Add stove to the array
            }
            if (object.getName().equals("chopping_board")) {
                 x = object.getProperties().get("x", Float.class)+70;
                 y = object.getProperties().get("y", Float.class)+25;
                 width = object.getProperties().get("width", Float.class);
                 height = object.getProperties().get("height", Float.class);

                ChoppingBoard choppingBoard = new ChoppingBoard(x, y, (int) width, (int) height, batch);
                chopping_boards.add(choppingBoard); // Add stove to the array
            }
            if (object.getName().equals("rice_cooker")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+40;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                RiceCooker riceCooker = new RiceCooker(x, y, (int) width, (int) height, batch);
                rice_cookers.add(riceCooker); // Add stove to the array
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
                food_sources.add(onion); // Add onion to the array
            }else if (object.getName().equals("meat_source")) {
                x = object.getProperties().get("x", Float.class)+16;
                y = object.getProperties().get("y", Float.class)+40;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource meat = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.meat);
                food_sources.add(meat); // Add meat to the array
            }else if (object.getName().equals("fish_tank_source")) {
                x = object.getProperties().get("x", Float.class)+32;
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource fish = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.fish);
                food_sources.add(fish); // Add stove to the array
            }else if (object.getName().equals("tomato_source")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource tomato = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.tomato);
                food_sources.add(tomato); // Add tomato to the array
            }
            else if (object.getName().equals("pickle_source")) {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class)+55;
                width = object.getProperties().get("width", Float.class);
                height = object.getProperties().get("height", Float.class);

                FoodSource pickle = new FoodSource(x, y, (int) width, (int) height, batch,FoodType.pickle);
                food_sources.add(pickle); // Add pickle to the array
            }
        }
    }
}
