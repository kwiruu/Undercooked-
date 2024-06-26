package com.libgdx.undercooked;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.Npc.Npc;
import com.libgdx.undercooked.screen.GameScreen;
import com.libgdx.undercooked.utils.TiledObjectUtil;

import static com.libgdx.undercooked.entities.PlayerManager.Player.player;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class MapManager {
    private final TiledMap map;
    private final Texture[] test_map_textures;
    public static OrthogonalTiledMapRenderer tmr;


    public MapManager(World world, SpriteBatch batch, Npc npcManager) {  // Add NPC manager to constructor

        String selectedMap = getSelectedMap();
        System.out.println(selectedMap);
        map = new TmxMapLoader().load("assets/maps/" + selectedMap + ".tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision_layer").getObjects());

        test_map_textures = new Texture[]{
            new Texture("assets/maps/" + selectedMap + "/blackwall.png"),
            new Texture("assets/maps/" + selectedMap + "/wall.png"),
            new Texture("assets/maps/" + selectedMap + "/furnitures.png"),
            new Texture("assets/maps/" + selectedMap + "/on_top.png"),
            new Texture("assets/maps/" + selectedMap + "/behind_player.png"),
        };

    }

    public void drawLayerTextures(SpriteBatch batch, TextureRegion textregion) {
        for (int i = 0; i < test_map_textures.length; i++) {
            Texture texturez = test_map_textures[i];
            if (i == 4) { // Check if it's the layer for the player
                batch.draw(textregion, player.getPosition().x * PPM - ((float) textregion.getRegionWidth() / 2), player.getPosition().y * PPM - ((float) textregion.getRegionHeight() / 8));
            }
            batch.draw(texturez, 0, 0);
        }
    }



    public static void dispose() {
        tmr.dispose();
    }

    public TiledMap getMap() {
        return map;
    }

}
