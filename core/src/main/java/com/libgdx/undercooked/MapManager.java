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

import static com.libgdx.undercooked.PlayerManager.hasItemz;
import static com.libgdx.undercooked.PlayerManager.player;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class MapManager {

    private final TiledMap map;
    private final Texture[] test_map_textures;
    public OrthogonalTiledMapRenderer tmr;
    private final EntityList entityList;
    private Animation<TextureRegion> smokeAnimation;
    private float stateTime; // State time for the smoke animation

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

        // Load smoke animation frames
        TextureAtlas smokeAtlas = new TextureAtlas(Gdx.files.internal("assets/fx_sprites/fxAtlas.atlas"));
        Array<TextureAtlas.AtlasRegion> smokeRegions = smokeAtlas.findRegions("poof"); // Assuming "poof" is the name of your region
        smokeAnimation = new Animation<>(0.2f, smokeRegions); // Create the animation with all frames
        stateTime = 0f; // Initialize state time for animation

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
                        batch.draw(texture, itemX, itemY);
                    } else {
                        // Log a warning if the texture is outside the viewport
                        Gdx.app.log("Warning", "Texture outside viewport");
                    }

                    // Draw the current frame of the smoke animation at the specified position
                    TextureRegion currentFrame = smokeAnimation.getKeyFrame(stateTime, true); // true for looping
                    batch.draw(currentFrame, itemX-8, itemY-8,48,48);
                }
            }
            batch.draw(texturez, 0, 0);
        }
        entityList.render();
        stateTime += Gdx.graphics.getDeltaTime(); // Update state time for the next frame
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
