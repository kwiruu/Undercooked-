package com.libgdx.undercooked;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.EntityList;
import com.libgdx.undercooked.utils.TiledObjectUtil;

import static com.libgdx.undercooked.PlayerManager.hasItemz;
import static com.libgdx.undercooked.PlayerManager.player;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class MapManager {

    private final TiledMap map;
    private final Texture[] test_map_textures;
    public OrthogonalTiledMapRenderer tmr;
    private final EntityList entityList;

    public MapManager(World world, SpriteBatch batch) {
        map = new TmxMapLoader().load("assets/maps/test_map.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledObjectUtil.parseTiledObjectLayer(world, map.getLayers().get("collision_layer").getObjects());

        test_map_textures = new Texture[] {
            new Texture("assets/maps/test_map/test_map_blackwall.png"),
            new Texture("assets/maps/test_map/test_map_wall.png"),
            new Texture("assets/maps/test_map/test_map_furnitures.png"),
            new Texture("assets/maps/test_map/test_map_on-top.png"),
            new Texture("assets/maps/test_map/test_map_behind_player.png"),
        };

        entityList = new EntityList(map, batch);
    }

    public void drawLayerTextures(SpriteBatch batch, TextureRegion textregion) {
        for (int i = 0; i < test_map_textures.length; i++) {
            Texture texturez = test_map_textures[i];
            if (i == 4) { // Check if it's the layer for the player
                String itemHeld = hasItemz;
                batch.draw(textregion, player.getPosition().x * PPM - ((float) textregion.getRegionWidth() / 2), player.getPosition().y * PPM - ((float) textregion.getRegionHeight() / 8));
                if (!itemHeld.isEmpty()) {
                    Texture texture = new Texture(Gdx.files.internal("assets/food_sprites/raw_sprites/" + itemHeld + ".png"));
                    float itemX = (player.getPosition().x - 0.4f) * PPM; // Adjust X position to center above the player
                    float itemY = (player.getPosition().y + 1.5f) * PPM; // Adjust Y position to above the player

                    // Ensure texture is loaded
                    if (!texture.getTextureData().isPrepared()) {
                        texture.getTextureData().prepare();
                    }

                    // Check if the texture fits within the viewport
                    if (itemX >= 0 && itemX <= Gdx.graphics.getWidth() && itemY >= 0 && itemY <= Gdx.graphics.getHeight()) {
                        batch.draw(texture, itemX, itemY, 24, 24);
                    } else {
                        // Log a warning if the texture is outside the viewport
                        Gdx.app.log("Warning", "Texture outside viewport");
                    }
                }
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
