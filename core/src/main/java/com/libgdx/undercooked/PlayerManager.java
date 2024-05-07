package com.libgdx.undercooked;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.HashMap;

import static com.libgdx.undercooked.utils.Constants.PPM;

public class PlayerManager {
    public static Body player;
    private Texture texture;
    private SpriteBatch playerBatch;
    private TextureAtlas textureAtlas;
    private String lastDirection;


    public PlayerManager(World world, TextureAtlas textureAtlasz) {
        this.textureAtlas = textureAtlasz;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        texture = new Texture("assets/sprites/Chef1/idle_down_01.png");
        player = createBox(world, 8, 2, 16, 8, false);
        playerBatch = new SpriteBatch();
        lastDirection = "down";
    }


    public SpriteBatch getBatch(){
        return playerBatch;
    }

    public void inputUpdate(float deltaTime) {
        float horizontalForce = 0;
        float verticalForce = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            verticalForce += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            horizontalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            verticalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            horizontalForce += 1;
        }

        if (horizontalForce != 0 && verticalForce != 0) {
            verticalForce *= 0.7;
            horizontalForce *= 0.7;
        }

        player.setLinearVelocity(horizontalForce * 5, verticalForce * 5);
    }

    public void setLastDirection(String direction) {
        this.lastDirection = direction;
    }

    public String getLastDirection() {
        return lastDirection;
    }


    public Vector2 getPosition() {
        return player.getPosition();
    }

    public void dispose(){
        playerBatch.dispose();
        texture.dispose();
    }

    private Body createBox(World world, int x, int y, int width, int height, boolean isStatic) {
        Body pBody;
        BodyDef def = new BodyDef();

        if (isStatic)
            def.type = BodyDef.BodyType.StaticBody;
        else
            def.type = BodyDef.BodyType.DynamicBody;

        def.position.set(x, y);
        def.fixedRotation = true;
        pBody = world.createBody(def);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        pBody.createFixture(shape, 1.0f);

        shape.dispose();
        return pBody;
    }

}

