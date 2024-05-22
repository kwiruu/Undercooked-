package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class StationList {
    private final SpriteBatch batch;
    private  Array<Station> stationArray;
    private final TiledMap map;

    private Orders orders;

    public StationList(World world, TiledMap map, SpriteBatch batch, Orders orders) {
        this.map = map;
        this.batch = batch;
        this.orders = orders;
        stationArray = new Array<>();
        renderEntity(world, map);
    }
    public void render() {
        for (Station st : stationArray) {
            st.render();
        }
    }
    public void update(float deltaTime) {
        for (Station st : stationArray) {
            if (st instanceof canUpdate) {
                ((canUpdate) st).update(deltaTime);
            }
        }
    }

    private void renderEntity(World world, TiledMap map) {
        float x, y, width, height;
        MapLayer objectLayer = map.getLayers().get("station_layer");

        // kani kay for adding the stations to an array of different stations!
        // array stoves, chopping_boards, rice_cookers, food_sources!
        // it is called in the constructor then to be used in the mapManager

        for (MapObject object : objectLayer.getObjects()) {
            String objectName = object.getName();
            if (objectName != null) {
                // if the object is named stove in the tiled map then mu create siyag new nga stove!
                if (object.getName().equals("station_stove")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class) + 55;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Stove stove = new Stove(world, x, y, (int) width, (int) height, batch);
                    stationArray.add(stove);
                } else if (object.getName().equals("station_chopping_board")) {
                    x = object.getProperties().get("x", Float.class) + 16;
                    y = object.getProperties().get("y", Float.class) + 25;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    ChoppingBoard choppingBoard = new ChoppingBoard(world, x, y, (int) width, (int) height, batch);
                    stationArray.add(choppingBoard);
                } else if (object.getName().equals("station_rice_cooker")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class) + 40;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    RiceCooker riceCooker = new RiceCooker(world, x, y, (int) width, (int) height, batch);
                    stationArray.add(riceCooker);
                } else if (object.getName().equals("station_counter")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class) + 70;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Counter counter = new Counter(world, x, y, (int) width, (int) height, batch, orders);
                    stationArray.add(counter);
                } else if (object.getName().equals("station_placemat")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class);
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Placemat placemat = new Placemat(world, x, y, (int) width, (int) height, batch);
                    stationArray.add(placemat);
                } else if (object.getName().equals("station_speedbooster")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class);
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    SpeedBooster speedBooster = new SpeedBooster(world, x, y, (int) width, (int) height, batch);
                    stationArray.add(speedBooster);
                } else if (object.getName().equals("station_transporter")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class);
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Transporter transporter = new Transporter(world, x, y, (int) width, (int) height, batch, (int) x+32, (int) y);
                    stationArray.add(transporter);
                } else if (object.getName().equals("station_bin")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class);
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Bin bin = new Bin(world, x, y, (int) width, (int) height, batch);
                    stationArray.add(bin);
                } else if (object.getName().equals("station_polystation_sp")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class);
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Stove stove = new Stove(world, x, y, (int) width, (int) height, batch);
                    Placemat placemat = new Placemat(world, x, y, (int) width, (int) height, batch);
                    PolyStation polyStation = new PolyStation(world, x, y, (int) width, (int) height, batch, stove, placemat);
                    stationArray.add(polyStation);
                }

                //--------------------IMPORTANT-----------------------//

                // for the foodsources!!!
                // in the tiled map, we only need to name the foodsources like these!
                // more modular map creation!!!!!

                else if (object.getName().equals("onion_source")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class);
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    Stove stove = new Stove(world, x, y, (int) width, (int) height, batch);
                    Placemat placemat = new Placemat(world, x, y, (int) width, (int) height, batch);
                    PolyStation polyStation = new PolyStation(world, x, y, (int) width, (int) height, batch, stove, placemat);
                    stationArray.add(polyStation);

//                    x = object.getProperties().get("x", Float.class);
//                    y = object.getProperties().get("y", Float.class) + 55;
//                    width = object.getProperties().get("width", Float.class);
//                    height = object.getProperties().get("height", Float.class);
//
//                    FoodSource onion = new FoodSource(world, x, y, (int) width, (int) height, batch, FoodType.onion);
//                    stationArray.add(onion);
                } else if (object.getName().equals("meat_source")) {
                    x = object.getProperties().get("x", Float.class) + 48;
                    y = object.getProperties().get("y", Float.class) + 40;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    FoodSource meat = new FoodSource(world, x, y, (int) width, (int) height, batch, FoodType.meat);
                    stationArray.add(meat);
                } else if (object.getName().equals("fish_tank_source")) {
                    x = object.getProperties().get("x", Float.class) + 32;
                    y = object.getProperties().get("y", Float.class) + 55;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    FoodSource fish = new FoodSource(world, x, y, (int) width, (int) height, batch, FoodType.fish);
                    stationArray.add(fish);
                } else if (object.getName().equals("tomato_source")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class) + 55;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    FoodSource tomato = new FoodSource(world, x, y, (int) width, (int) height, batch, FoodType.tomato);
                    stationArray.add(tomato);
                } else if (object.getName().equals("pickle_source")) {
                    x = object.getProperties().get("x", Float.class);
                    y = object.getProperties().get("y", Float.class) + 55;
                    width = object.getProperties().get("width", Float.class);
                    height = object.getProperties().get("height", Float.class);

                    FoodSource pickle = new FoodSource(world, x, y, (int) width, (int) height, batch, FoodType.pickle);
                    stationArray.add(pickle);
                }
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
