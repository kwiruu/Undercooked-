package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
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
import com.libgdx.undercooked.MapManager;

import static com.libgdx.undercooked.GameManager.timesUp;
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
            initCamera();
            gameUI = new GameUI(context);
            Gdx.input.setInputProcessor(gameUI.getStage());
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
        }catch (NullPointerException e){
            context.setScreen(ScreenType.SELECTMAP);
        }
    }



    private void update(float deltaTime) {
        gameManager.update(deltaTime);
        cameraUpdate(deltaTime);
        MapManager.tmr.setView(camera);
        gameUI.update(gameManager.getPlayerManager());
        if (timesUp) {
            timesUp = false;
            System.out.println("Time's up! Switching to SELECTMAP screen.");
            gameManager.dispose();
            gameManager = null;
            try {
                context.setScreen(SelectionScreen.class.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }


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

    public void initCamera(){
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        float SCALE = 1.5f;
        camera.setToOrtho(false, w / SCALE, h / SCALE);
        viewport = new FitViewport(w / SCALE, h / SCALE, camera);
    }

    @Override
    public void dispose() {
        if (gameManager != null) {
            gameManager.dispose();
        }
    }
}
