package com.libgdx.undercooked;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.entities.Stove;

import static com.libgdx.undercooked.utils.Constants.PPM;

/** {@link ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private final float SCALE= 1.5f;
    private OrthographicCamera camera;
    private MapManager map;
    private Box2DDebugRenderer b2dr;
    private World world;
    private PlayerManager player;
    private SpriteBatch batch;
    private float elapsedTime = 0f;
    private Array<Stove> stove; // init stove

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/SCALE, h/SCALE);

        world = new World(new Vector2(0f,0f), false);
        b2dr = new Box2DDebugRenderer();

        player = new PlayerManager(world);
        player.run();
        batch = player.getBatch();

        map = new MapManager(world);

        //create stove
        stove = new Array<Stove>();
        stove.addAll(map.getStoves());
        //create stove

    }
    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();

        Animation<TextureRegion> currentAnimation = player.determineCurrentAnimation();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(elapsedTime, true); // 'true' for looping

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.drawLayerTextures(batch, currentFrame);

        // stove render
        for (Stove stove : stove) {
            stove.render(batch);
        }
        // stove render
        batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    private void update(float deltaTime) {
        world.step(1/60f, 6, 2);
        player.inputUpdate(deltaTime);
        cameraUpdate(deltaTime);
        map.tmr.setView(camera);
    }

    public void cameraUpdate(float deltaTime){
        Vector2 position = player.getPosition();
        camera.position.set(position.x * PPM, position.y * PPM, 0);
        camera.update();
    }
    @Override
    public void resize(int width, int height){
        camera.setToOrtho(false,width/ SCALE,height/ SCALE);
    }
    @Override
    public void dispose() {
        world.dispose();
        b2dr.dispose();
        player.dispose();
        map.dispose();
        batch.dispose();
    }
}
