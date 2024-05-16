package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.libgdx.undercooked.GameManager;
import com.libgdx.undercooked.Main;

import static com.libgdx.undercooked.MapManager.tmr;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class GameScreen extends ScreenAdapter {
    private final Main context;
    public static OrthographicCamera camera;
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
            gameManager = new GameManager();
            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();
            camera = new OrthographicCamera();
            float SCALE = 1.5f;
            camera.setToOrtho(false, w / SCALE, h / SCALE);
            viewport = new FitViewport(w / SCALE, h / SCALE, camera);
            gameUI = new GameUI(context);
        }
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();
        Animation<TextureRegion> currentAnimation = gameManager.getPlayerManager().determineCurrentAnimation();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(elapsedTime, true); // 'true' for looping
        SpriteBatch batch = gameManager.getBatch();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        gameManager.getMapManager().drawLayerTextures(batch, currentFrame);
        gameManager.getPlayerManager().renderItem(batch);
        batch.end();
        gameUI.render();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.LOADING);
        }
    }



    private void update(float deltaTime) {
        gameManager.getWorld().step(1 / 60f, 6, 2);
        gameManager.getPlayerManager().inputUpdate(deltaTime);
        gameManager.getPlayerManager().renderItemUpdate(deltaTime);
        cameraUpdate(deltaTime);
        tmr.setView(camera);
        gameManager.getMapManager().getEntityList().update();
        gameUI.update(gameManager.getPlayerManager());
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

    @Override
    public void dispose() {
        gameManager.dispose();
    }
}
