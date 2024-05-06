package com.libgdx.undercooked.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class MapRenderer {
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap map;
    private Texture[] backgroundTextures;

    public MapRenderer(TiledMap map, Texture[] backgroundTextures) {
        this.map = map;
        this.backgroundTextures = backgroundTextures;
        mapRenderer = new OrthogonalTiledMapRenderer(map);
    }

    public void render(SpriteBatch batch) {
        // Render background textures
        for (Texture texture : backgroundTextures) {
            batch.draw(texture, 0, 0);
        }

        // Render map layers
        mapRenderer.render();
    }

    public void dispose() {
        mapRenderer.dispose();
    }
}

