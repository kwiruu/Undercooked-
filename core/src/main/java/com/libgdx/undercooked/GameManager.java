
package com.libgdx.undercooked;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.libgdx.undercooked.entities.Orders;
import com.libgdx.undercooked.entities.EntityList;
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
import com.libgdx.undercooked.screen.FinishScreen;
import com.libgdx.undercooked.screen.ScreenType;

import static com.libgdx.undercooked.entities.Orders.totalOrders;

public class GameManager implements Disposable {

    private final World world;

    private static boolean checkEntry;
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
    private EntityList entityList;

    public static boolean win;
    private Orders orders;
    private UIUpdater uiUpdater;
    private final Main context;


    public GameManager(Main context, UIUpdater uiUpdater) {
        this.context = context;
        this.world = new World(new Vector2(0f, 0f), false);
        this.uiUpdater = uiUpdater;
        initialize();
    }
    private void initialize() {
        if (!initialized) {
            playerManager = new Player(world);
            playerManager.run();
            batch = playerManager.getBatch();
            npcManager = new Npc(world);
            score = 0;


            // Load NPC texture
            npcTexture = new Texture(Gdx.files.internal("assets/sprites/Chef2/idle_down_01.png"));
            Sprite npcSprite = new Sprite(npcTexture);
            createAndAddNpc(new Vector2(96, 72), npcSprite);
            System.out.println("Created ");

            mapManager = new MapManager(world, batch, npcManager);  // Pass NPC manager to MapManager

            orders = new Orders();
            entityList = new EntityList(world, mapManager.getMap(), batch, orders, playerManager);
            playerManager.setEntityList(entityList);

            initialized = true;
            checkEntry = true;
            win = false;
        }
    }

    private void createAndAddNpc(Vector2 spawnLocation, Sprite sprite) {
        npcManager.createNpc(spawnLocation, sprite, false);
    }

    private void checkCompletion() {
        if (totalOrders == 0) {
            timesUp = true;
            checkEntry = true;
            win = true;
            Orders.noOrders = true;
            System.out.println("checkCompletion is true!");
        }
    }


    public void update(float deltaTime) {
        checkCompletion();
        world.step(1 / 60f, 6, 2);
        playerManager.inputUpdate(deltaTime);
        playerManager.renderItemUpdate(deltaTime);
        npcManager.update(deltaTime);
        entityList.update(deltaTime, playerManager);
        orders.update(deltaTime, entityList);
        if (checkEntry) {
            uiUpdater.updateOrdersUI(orders); // Update the orders UI
            checkEntry = false;
        }
    }

    public void render(TextureRegion currentFrame) {
        getMapManager().drawLayerTextures(batch, currentFrame);
        npcManager.render(batch);
        entityList.render();
        getPlayerManager().render(batch);
    }

    public Player getPlayerManager() {
        return playerManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public Orders getOrders() {
        return orders;
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

    public boolean getWin(){
        return win;
    }

    public static void setCheckEntry(Boolean x){
        checkEntry = x;
    }
}
