package com.libgdx.undercooked;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.entities.EntityList;
import com.libgdx.undercooked.utils.TiledObjectUtil;

import static com.libgdx.undercooked.PlayerManager.player;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;
import static com.libgdx.undercooked.utils.Constants.PPM;
public class MapManager {

    private final TiledMap map;
    private final Texture[] test_map_textures;
    public OrthogonalTiledMapRenderer tmr;
    private final EntityList entityList;

    public MapManager(World world, SpriteBatch batch) {

        String selectedMap = getSelectedMap();
        System.out.println(selectedMap);
        map = new TmxMapLoader().load("assets/maps/"+selectedMap+".tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision_layer").getObjects());

        test_map_textures = new Texture[] {
            new Texture("assets/maps/"+selectedMap+"/blackwall.png"),
            new Texture("assets/maps/"+selectedMap+"/wall.png"),
            new Texture("assets/maps/"+selectedMap+"/furnitures.png"),
            new Texture("assets/maps/"+selectedMap+"/on_top.png"),
            new Texture("assets/maps/"+selectedMap+"/behind_player.png"),
        };

        entityList = new EntityList(map, batch);

    }



    public void drawLayerTextures(SpriteBatch batch, TextureRegion textregion) {
        for (int i = 0; i < test_map_textures.length; i++) {
            Texture texturez = test_map_textures[i];
            if (i == 4) { // Check if it's the layer for the player
                batch.draw(textregion, player.getPosition().x * PPM - ((float) textregion.getRegionWidth() / 2), player.getPosition().y * PPM - ((float) textregion.getRegionHeight() / 8));
            }
            batch.draw(texturez, 0, 0);
        }
        entityList.render();
    }

    public void dispose() {
        tmr.dispose();
    }

    public TiledMap getMap() {
        return map;
    }

    public EntityList getEntityList() {
        return entityList;
    }
}

