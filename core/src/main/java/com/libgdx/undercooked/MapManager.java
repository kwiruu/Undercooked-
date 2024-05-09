package com.libgdx.undercooked;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.entities.Stove;
import com.libgdx.undercooked.utils.TiledObjectUtil;
import static com.libgdx.undercooked.PlayerManager.player;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class MapManager {

    private TiledMap map;
    Texture[] test_map_textures;
    OrthogonalTiledMapRenderer tmr;
    private Array<Stove> stoves;

    // bag o ni bows
    private Stove stove;
    public MapManager(World world) {

        map = new TmxMapLoader().load("assets/maps/test_map.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledObjectUtil.parseTiledObjectLayer(world,map.getLayers().get("collision_layer").getObjects());

        test_map_textures = new Texture[] {
            new Texture("assets/maps/test_map/test_map_blackwall.png"),
            new Texture("assets/maps/test_map/test_map_wall.png"),
            new Texture("assets/maps/test_map/test_map_furnitures.png"),
            new Texture("assets/maps/test_map/test_map_on-top.png"),
            new Texture("assets/maps/test_map/test_map_behind_player.png"),
        };

        stoves = new Array<Stove>();
        populateStoves();
    }

    private void populateStoves() {
        // for stove rendering & location!
        float stovex = 0; // Set default values
        float stovey = 0;
        float stoveWidth = 32;
        float stoveHeight = 64;

        MapLayer objectLayer = map.getLayers().get("station_layer");
        // Get the different stations
        for (MapObject object : objectLayer.getObjects()) {
            if (object.getName().equals("stove")) {
                // Retrieve properties...
                stovex = object.getProperties().get("x", Float.class); // add num to render on on desired location!!
                stovey = object.getProperties().get("y", Float.class) + 55;
                stoveWidth = object.getProperties().get("width", Float.class);
                stoveHeight = object.getProperties().get("height", Float.class);

                Stove stove = new Stove(stovex, stovey, (int) stoveWidth, (int) stoveHeight);
                stoves.add(stove); // Add stove to the array
            }
        }
    }


    public void drawLayerTextures(SpriteBatch batch,TextureRegion textregion) {
        for (int i = 0; i < test_map_textures.length; i++) {
            Texture texturez = test_map_textures[i];
            if (i == 4) { // Check if it's the layer for the player
                batch.draw(textregion, player.getPosition().x * PPM - ((float) textregion.getRegionWidth() / 2), player.getPosition().y * PPM - ((float) textregion.getRegionHeight() / 8));
            }
            batch.draw(texturez, 0, 0);
        }
    }
    public void dispose(){
        tmr.dispose();
    }

    public Stove getStove(){
        return stove;
    }

    public Array<Stove> getStoves() {
        return stoves;
    }
}
