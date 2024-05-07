package com.libgdx.undercooked;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.libgdx.undercooked.utils.TiledObjectUtil;

import static com.libgdx.undercooked.utils.Constants.PPM;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private boolean DEBUG = false;
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

    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/SCALE, h/SCALE);

        world = new World(new Vector2(0f,0f), false);
        b2dr = new Box2DDebugRenderer();

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
    }
    @Override
    public void render() {

        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // for test_map drawing/rendering!!!
        int i=0;
        for (Texture texturez : test_map_textures) {
            i++;
            if(i==5){
                // gamit ani kay e check niya if naa nakas behind_player nga index,
                // if so then e draw niya ang player first!
                batch.draw(texture, player.getPosition().x * PPM - (texture.getWidth() / 2), player.getPosition().y * PPM - (texture.getHeight() / 8));
                batch.draw(texturez, 0, 0);
            }
            else{
                batch.draw(texturez, 0, 0);
            }
        }
        // end or test_map rendering!!!
        batch.end();
        b2dr.render(world, camera.combined.scl(PPM));

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    private void update(float deltaTime) {
        world.step(1/60f, 6, 2);
        player.inputUpdate(deltaTime);
        cameraUpdate(deltaTime);
        tmr.setView(camera);
        batch.setProjectionMatrix(camera.combined);
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
        tmr.dispose();
        map.dispose();
    }
}
