package com.libgdx.undercooked;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import static com.libgdx.undercooked.utils.Constants.PPM;

//import static jdk.jfr.internal.consumer.EventLog.update;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private boolean DEBUG = false;
    private final float SCALE= 2.0f;
    private OrthographicCamera camera;
    private Box2DDebugRenderer b2dr;

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

        world = new World(new Vector2(0,0f), false);
        b2dr = new Box2DDebugRenderer();

        player = createBox(2,10,32,64,false);
        Body platform = createBox(0, 0, 64, 32, true);

        batch = new SpriteBatch();
        texture = new Texture("assets/sprites/Chef1/idle_down_01.png");
    }

    @Override
    public void render() {

        update(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(texture,player.getPosition().x * PPM - (texture.getWidth()/2),player.getPosition().y * PPM - (texture.getHeight()/2));
        batch.end();

        b2dr.render(world, camera.combined.scl(PPM));

        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }

    private void update(float deltaTime) {
        world.step(1/60f, 6, 2);

        inputUpdate(deltaTime);
        cameraUpdate(deltaTime);

        batch.setProjectionMatrix(camera.combined);
    }

    public void inputUpdate(float deltaTime){
        int horizontalForce = 0;
        int verticalForce = 0;

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
    }

    public Body createBox(int x, int y, int width, int height, boolean isStatic){
        Body pBody;
        BodyDef def = new BodyDef();

        if(isStatic)
            def.type = BodyDef.BodyType.StaticBody;
        else
            def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x/PPM,y/PPM);
        def.fixedRotation = true;
        pBody = world.createBody(def);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2 / PPM,height/2/ PPM);

        pBody.createFixture(shape,1.0f);

        shape.dispose();
        return pBody;
    }

}
