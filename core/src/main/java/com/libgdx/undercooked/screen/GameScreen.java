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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.libgdx.undercooked.Main;
import com.libgdx.undercooked.MapManager;
import com.libgdx.undercooked.PlayerManager;

import static com.libgdx.undercooked.utils.Constants.PPM;

public class GameScreen extends ScreenAdapter {

    private final Main context;
    private OrthographicCamera camera;
    private MapManager map;
    private Box2DDebugRenderer b2dr;
    private World world;
    private PlayerManager player;
    private SpriteBatch batch;
    private float elapsedTime = 0f;
    private FitViewport viewport;
    private boolean initialized = false; // To track initialization

    public GameScreen(final Main context) {
        this.context = context;
    }

    @Override
    public void show() {
        if (!initialized) {
            float w = Gdx.graphics.getWidth();
            float h = Gdx.graphics.getHeight();

            camera = new OrthographicCamera();
            float SCALE = 1.5f;
            camera.setToOrtho(false, w / SCALE, h / SCALE);

            world = new World(new Vector2(0f, 0f), false);
            b2dr = new Box2DDebugRenderer();

            player = new PlayerManager(world);
            player.run();
            batch = player.getBatch();

            map = new MapManager(world,batch);

            player.setEntityList(map.getEntityList());

            viewport = new FitViewport(1400, 800);
            initialized = true;
        }
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(true);

        elapsedTime += Gdx.graphics.getDeltaTime();

        Animation<TextureRegion> currentAnimation = player.determineCurrentAnimation();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(elapsedTime, true); // 'true' for looping

        batch.begin();

        batch.setProjectionMatrix(camera.combined);

        map.drawLayerTextures(batch, currentFrame);

        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.LOADING);
        }
    }

    private void update(float deltaTime) {
        world.step(1 / 60f, 6, 2);
        player.inputUpdate(deltaTime);
        cameraUpdate(deltaTime);
        map.tmr.setView(camera);
    }

    public void cameraUpdate(float deltaTime) {
        Vector2 position = player.getPosition();
        camera.position.set(position.x * PPM, position.y * PPM, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        b2dr.dispose();
        batch.dispose();
    }
}
