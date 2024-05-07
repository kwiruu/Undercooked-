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

//import static jdk.jfr.internal.consumer.EventLog.update;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private boolean DEBUG = false;
    private final float SCALE= 2.0f;
    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer tmr;
    private TiledMap map;
    private Box2DDebugRenderer b2dr;
    private Texture backgroundTexture1,backgroundTexture2,backgroundTexture3,backgroundTexture4,backgroundTexture5;

    private World world;
    private Body player;
    private SpriteBatch batch;
    private Texture texture;


    @Override
    public void create() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, w/SCALE, h/SCALE);

        world = new World(new Vector2(0f,0f), false);
        b2dr = new Box2DDebugRenderer();

        player = createBox(8,2,16,8,false);
        Body platform = createBox(8, 0, 64, 32, true);

        batch = new SpriteBatch();
        texture = new Texture("assets/sprites/Chef1/idle_down_01.png");

        map = new TmxMapLoader().load("assets/maps/test_map.tmx");
        tmr = new OrthogonalTiledMapRenderer(map);

        // ako nalang ge image kay impossible ang 2k nga sriptes lmao
        backgroundTexture1 = new Texture("assets/maps/test_map/test_map_blackwall.png");
        backgroundTexture2 = new Texture("assets/maps/test_map/test_map_wall.png");
        backgroundTexture3 = new Texture("assets/maps/test_map/test_map_behind_player.png");
        backgroundTexture4 = new Texture("assets/maps/test_map/test_map_furnitures.png");
        backgroundTexture5 = new Texture("assets/maps/test_map/test_map_on-top.png");

        TiledObjectUtil.parseTiledObjectLayer(world,map.getLayers().get("collision_layer").getObjects());
    }

    @Override
    public void render() {

        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(58 / 255f, 58 / 255f, 80 / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // manually add the images by layer!
        batch.draw(backgroundTexture1, 0, 0);
        batch.draw(backgroundTexture2, 0, 0);
        batch.draw(texture, player.getPosition().x * PPM - (texture.getWidth() / 2), player.getPosition().y * PPM - (texture.getHeight() / 8));
        batch.draw(backgroundTexture3, 0, 0);
        batch.end();

        for (int i = 0; i < map.getLayers().getCount(); i++) {
            if (shouldRenderLayer(i)) {
                tmr.renderTileLayer((TiledMapTileLayer) map.getLayers().get(i));
            }
        }

        b2dr.render(world, camera.combined.scl(PPM));

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    private boolean shouldRenderLayer(int layerIndex) {
        // Add your logic here to determine which layers to render
        // For example, you can check the layer names or indices
        // Return true for layers you want to render, false for others
        String layerName = map.getLayers().get(layerIndex).getName();
        return layerName.equals("background_layer") || layerName.equals("object_layer");
    }

    private void update(float deltaTime) {
        world.step(1/60f, 6, 2);

        inputUpdate(deltaTime);
        cameraUpdate(deltaTime);
        tmr.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    public void inputUpdate(float deltaTime){

        float horizontalForce = 0;
        float verticalForce = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.W)){
            verticalForce += 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A)){
            horizontalForce -= 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)){
            verticalForce -=1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D)){
            horizontalForce += 1;
        }

        player.setLinearVelocity(horizontalForce * 5, player.getLinearVelocity().y);
        player.setLinearVelocity(player.getLinearVelocity().x,verticalForce * 5);
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
        batch.dispose();
        texture.dispose();
        backgroundTexture1.dispose();
        backgroundTexture2.dispose();
        backgroundTexture3.dispose();
        backgroundTexture4.dispose();
        backgroundTexture5.dispose();
        tmr.dispose();
        map.dispose();


    }

    public Body createBox(int x, int y, int width, int height, boolean isStatic){
        Body pBody;
        BodyDef def = new BodyDef();

        if(isStatic)
            def.type = BodyDef.BodyType.StaticBody;
        else
            def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x,y);
        def.fixedRotation = true;
        pBody = world.createBody(def);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2 / PPM,height/2/ PPM);

        pBody.createFixture(shape,1.0f);

        shape.dispose();
        return pBody;
    }

}
