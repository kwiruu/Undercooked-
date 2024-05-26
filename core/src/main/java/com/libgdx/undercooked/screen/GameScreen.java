package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.libgdx.undercooked.AudioManager.MapSound;
import com.libgdx.undercooked.GameManager;
import com.libgdx.undercooked.Main;
import com.libgdx.undercooked.MapManager;
import com.libgdx.undercooked.entities.Orders;

import static com.libgdx.undercooked.AudioManager.MapSound.mapRunning;
import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.GameManager.timesUp;
import static com.libgdx.undercooked.screen.LandingPageScreen.getUsername;
import static com.libgdx.undercooked.screen.SelectionScreen.mapId;
import static com.libgdx.undercooked.utils.Constants.PPM;
import static database.SQLOperations.*;

public class GameScreen extends ScreenAdapter {
    private final Main context;
    public static OrthographicCamera camera;
    private Box2DDebugRenderer debugRenderer;
    private FitViewport viewport;
    private GameUI gameUI;
    private GameManager gameManager;
    private float elapsedTime = 0f;

    public GameScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        if (gameManager == null) {
            initCamera();
            gameUI = new GameUI(context);
            // Pass the Main context to the GameManager
            gameManager = new GameManager(context, gameUI);
            Gdx.input.setInputProcessor(gameUI.getStage());
            debugRenderer = new Box2DDebugRenderer();
        }
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();
        try {
            Animation<TextureRegion> currentAnimation = gameManager.getPlayerManager().determineCurrentAnimation();
            TextureRegion currentFrame = currentAnimation.getKeyFrame(elapsedTime, true); // 'true' for looping
            SpriteBatch batch = gameManager.getBatch();
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            gameManager.render(currentFrame);
            batch.end();
            gameUI.render();
        } catch (NullPointerException e) {
            // Handle exception if gameManager is null
            Gdx.app.log("GameScreen","Game Ended!");
        }
    }

    private void update(float deltaTime) {
        gameManager.update(deltaTime);
        cameraUpdate(deltaTime);

        Gdx.app.log("GameScreen"," : "+score);
        MapManager.tmr.setView(camera);
        gameUI.update(gameManager.getPlayerManager());
        if (timesUp) {
            if (gameManager.getWin()) {
                System.out.println(score);
                insertScore(getUsername(), 1, (int) (180 - gameUI.getElapsedTime() + score));
                levelUp(getUsername(), findLevel(getUsername()));
                context.setScreen(new FinishScreen(context, elapsedTime));
            }
            // Switch to the FinishScreen
            context.setScreen(new FinishScreen(context, elapsedTime));
        }
    }

    private void cameraUpdate(float deltaTime) {
        Vector2 position = gameManager.getPlayerManager().getPosition();
        camera.position.set(position.x * PPM, position.y * PPM, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void initCamera() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        float SCALE = 1.5f;
        camera.setToOrtho(false, w / SCALE / PPM, h / SCALE / PPM);
        viewport = new FitViewport(w / SCALE, h / SCALE, camera);
    }

    @Override
    public void dispose() {
        if (gameManager != null) {
            gameManager.dispose();
            debugRenderer.dispose();
        }
    }

    public void finishGame() {
        Orders.freeOrderList();
        timesUp = false;
        gameManager.dispose();
        gameManager = null;
        mapRunning = false;
        MapSound.stop();
        mapId = 0;
    }
}
