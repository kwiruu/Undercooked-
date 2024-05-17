package com.libgdx.undercooked;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.libgdx.undercooked.entities.StationList;

import com.libgdx.undercooked.entities.Npc.components.NpcB2D;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.libgdx.undercooked.entities.Npc.Npc;

public class GameManager implements Disposable {

    private final World world;
    private MapManager mapManager;
    private Player playerManager;
    private SpriteBatch batch;
    private boolean initialized = false;
    private float elapsedTime = 0f;
    private float TIME_LIMIT = 10f;
    public static boolean timesUp = false;

    public static int score = 0;
    public static NpcB2D npc;
    private Npc npcManager;
    private Texture npcTexture;
    private StationList stationList;
    public GameManager() {
        this.world = new World(new Vector2(0f, 0f), false);
        initialize();
    }

    private void initialize() {
        if (!initialized) {
            playerManager = new Player(world);
            playerManager.run();
            batch = playerManager.getBatch();
            npcManager = new Npc(world);

            // Load NPC texture
            npcTexture = new Texture(Gdx.files.internal("assets/sprites/Chef2/idle_down_01.png"));
            Sprite npcSprite = new Sprite(npcTexture);
            createAndAddNpc(new Vector2(96, 72), npcSprite);
            System.out.println("CReated ");

            mapManager = new MapManager(world, batch, npcManager);  // Pass NPC manager to MapManager

            stationList = new StationList(world, mapManager.getMap(), batch);
            playerManager.setEntityList(stationList);

            initialized = true;
        }
    }

    private void createAndAddNpc(Vector2 spawnLocation, Sprite sprite) {
       npcManager.createNpc(spawnLocation, sprite,false);
    }

    public void update(float deltaTime) {
//        elapsedTime += deltaTime; // Increment elapsed time
//        TIME_LIMIT -= deltaTime;
//        if (TIME_LIMIT < elapsedTime) {
//            timesUp = true;
//            return;
//        }

        world.step(1 / 60f, 6, 2);
        playerManager.inputUpdate(deltaTime);
        playerManager.renderItemUpdate(deltaTime);
        npcManager.update(deltaTime);
        stationList.update();
        // Add any necessary updates for NPCs here
    }

    public void render(TextureRegion currentFrame) {
        getMapManager().drawLayerTextures(batch, currentFrame);
        getPlayerManager().renderItem(batch);
        npcManager.render(batch);
        stationList.render();
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
        npcTexture.dispose();
        initialized = false;
        Gdx.app.log("GameManager", "World disposed after 3 minutes");
    }
}
