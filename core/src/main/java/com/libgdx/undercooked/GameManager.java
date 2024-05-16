package com.libgdx.undercooked;

import PlayerManager.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class GameManager implements Disposable {

    private final World world;
    private MapManager mapManager;
    private Player playerManager;
    private SpriteBatch batch;
    private boolean initialized = false;
    private float elapsedTime = 0f;
    private float TIME_LIMIT = 10f;

    public static boolean timesUp = false;

    public GameManager() {
        this.world = new World(new Vector2(0f, 0f), false);
        initialize();
    }

    private void initialize() {
        if (!initialized) {
            playerManager = new Player(world);
            playerManager.run();
            batch = playerManager.getBatch();
            mapManager = new MapManager(world, batch);
            playerManager.setEntityList(mapManager.getEntityList());
            initialized = true;
        }
    }

    public void update(float deltaTime) {
        TIME_LIMIT -= deltaTime;
        if (TIME_LIMIT < elapsedTime) {
            timesUp = true;
            return;
        }

        world.step(1 / 60f, 6, 2);
        playerManager.inputUpdate(deltaTime);
        playerManager.renderItemUpdate(deltaTime);
        mapManager.getEntityList().update();
    }

    public void render() {
        System.out.println("WAKOY GAMIT");
        batch.begin();
        mapManager.drawLayerTextures(batch, playerManager.determineCurrentAnimation().getKeyFrame(elapsedTime, true));
        playerManager.renderItem(batch);
        batch.end();
    }

    public Player getPlayerManager() {
        return playerManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void dispose() {
        MapManager.dispose();
        world.dispose();
        batch.dispose();
        initialized = false;
        Gdx.app.log("GameManager", "World disposed after 3 minutes");
    }
}
