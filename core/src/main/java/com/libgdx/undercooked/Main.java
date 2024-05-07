package com.libgdx.undercooked;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.libgdx.undercooked.utils.TiledObjectUtil;

import java.util.HashMap;
import java.util.Map;

import static com.libgdx.undercooked.utils.Constants.PPM;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private final float SCALE= 1.5f;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private Box2DDebugRenderer b2dr;
    private World world;
    private PlayerManager player;
    private SpriteBatch batch;
    private Texture texture;
    private Texture[] test_map_textures;
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> idle_down;
    private static final float frameDuration = 0.09f;
    private float elapsedTime = 0f;
    public TextureRegion currentFrame;

    private Map<String, Animation<TextureRegion>> animations;
    private String[] directions = {"top", "down", "left", "right"};


    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/SCALE, h/SCALE);
        world = new World(new Vector2(0f,0f), false);
        b2dr = new Box2DDebugRenderer();

        //for animation!
        textureAtlas = new TextureAtlas(Gdx.files.internal("assets/sprites/Chef1Atlas.atlas"));

        player = new PlayerManager(world);
        batch = player.getBatch();

        texture = new Texture("assets/sprites/Chef1/idle_down_01.png");
        map = new TmxMapLoader().load("assets/maps/test_map.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        TiledObjectUtil.parseTiledObjectLayer(world,map.getLayers().get("collision_layer").getObjects());


        // ako nalang ge image kay impossible ang 2k nga sriptes lmao
        // for text_map!
        test_map_textures = new Texture[] {
            new Texture("assets/maps/test_map/test_map_blackwall.png"),
            new Texture("assets/maps/test_map/test_map_wall.png"),
            new Texture("assets/maps/test_map/test_map_furnitures.png"),
            new Texture("assets/maps/test_map/test_map_on-top.png"),
            new Texture("assets/maps/test_map/test_map_behind_player.png"),
        };

        idle_down = new Animation<>(frameDuration, textureAtlas.findRegions("idle_down"));
        animations = new HashMap<>();
        //call all of the animations needed!
        initializeAnimations();
        TextureRegion currentFrame = new TextureRegion();
    }
    @Override
    public void render() {

        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        elapsedTime += Gdx.graphics.getDeltaTime();

        //get the animations
        Animation<TextureRegion> currentAnimation = determineCurrentAnimation();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(elapsedTime, true); // 'true' for looping

        batch.begin();

        // for test_map drawing/rendering!!!
        // test_map_texures if for the map layers & player layering, currentFrame is for the player animation!
        drawLayerTextures(test_map_textures, currentFrame);

        // end the test_map rendering!!!
        batch.end();

        //render this to show collision / objects!
        //b2dr.render(world, camera.combined.scl(PPM));

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    private void drawLayerTextures(Texture[] textures, TextureRegion textregion) {
        for (int i = 0; i < textures.length; i++) {
            Texture texturez = textures[i];
            if (i == 4) { // Check if it's the layer for the player
                batch.draw(textregion, player.getPosition().x * PPM - (textregion.getRegionWidth() / 2), player.getPosition().y * PPM - (textregion.getRegionHeight() / 8));
                batch.draw(texturez, 0, 0);
            } else {
                batch.draw(texturez, 0, 0);
            }
        }
    }

    private void update(float deltaTime) {
        world.step(1/60f, 6, 2);
        player.inputUpdate(deltaTime);
        cameraUpdate(deltaTime);
        tmr.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    private Animation<TextureRegion> determineCurrentAnimation() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setLastDirection("top");
            return animations.get("running_top");
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setLastDirection("left");
            return animations.get("running_left");
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setLastDirection("down");
            return animations.get("running_down");
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setLastDirection("right");
            return animations.get("running_right");
        } else {
            // If no movement keys are pressed, return the idle animation based on the last movement direction
            String lastDir = player.getLastDirection();
            if (lastDir != null) {
                return animations.get("idle_" + lastDir);
            } else {
                // Default to idle_down if no valid last movement direction is found
                return animations.get("idle_down");
            }
        }
    }

    public void cameraUpdate(float deltaTime){
        Vector3 position = camera.position;
        position.x = player.getPosition().x * PPM;
        position.y = player.getPosition().y * PPM;
        camera.position.set(position);
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
        texture.dispose();
        textureAtlas.dispose();
        tmr.dispose();
        map.dispose();
    }

    public void initializeAnimations() {
        System.out.println("initialiing animations!");
        // running anim
        animations.put("running_down", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("running_down")));
        animations.put("running_top", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("running_top")));
        animations.put("running_left", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("running_left")));
        animations.put("running_right", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("running_right")));
        // idle anim
        animations.put("idle_down", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("idle_down")));
        animations.put("idle_top", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("idle_up")));
        animations.put("idle_left", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("idle_left")));
        animations.put("idle_right", new com.badlogic.gdx.graphics.g2d.Animation<>(frameDuration, textureAtlas.findRegions("idle_right")));
    }
}
