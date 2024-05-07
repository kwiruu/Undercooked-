package com.libgdx.undercooked;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import java.util.HashMap;
import java.util.Map;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class PlayerManager {
    static Body player;
    private final TextureAtlas textureAtlas;
    private final SpriteBatch playerBatch;
    private String lastDirection;
    private final Map<String, Animation<TextureRegion>> animations;

    public PlayerManager(World world) {
        textureAtlas = new TextureAtlas(Gdx.files.internal("assets/sprites/Chef1Atlas.atlas"));
        player = createBox(world, 8, 2, 16, 8, false);
        playerBatch = new SpriteBatch();
        lastDirection = "down";
        animations = new HashMap<>();
        initializeAnimations();
    }

    public SpriteBatch getBatch() {
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
        shape.setAsBox((float) width / 2 / PPM, (float) height / 2 / PPM);

        pBody.createFixture(shape, 1.0f);

        shape.dispose();
        return pBody;
    }

    private void initializeAnimations() {
        // running anim
        animations.put("running_down", new Animation<>(0.09f, textureAtlas.findRegions("running_down")));
        animations.put("running_top", new Animation<>(0.09f, textureAtlas.findRegions("running_top")));
        animations.put("running_left", new Animation<>(0.09f, textureAtlas.findRegions("running_left")));
        animations.put("running_right", new Animation<>(0.09f, textureAtlas.findRegions("running_right")));
        // idle anim
        animations.put("idle_down", new Animation<>(0.09f, textureAtlas.findRegions("idle_down")));
        animations.put("idle_top", new Animation<>(0.09f, textureAtlas.findRegions("idle_up")));
        animations.put("idle_left", new Animation<>(0.09f, textureAtlas.findRegions("idle_left")));
        animations.put("idle_right", new Animation<>(0.09f, textureAtlas.findRegions("idle_right")));
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return determineCurrentAnimation();
    }

    Animation<TextureRegion> determineCurrentAnimation() {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            setLastDirection("top");
            return animations.get("running_top");
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            setLastDirection("left");
            return animations.get("running_left");
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            setLastDirection("down");
            return animations.get("running_down");
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            setLastDirection("right");
            return animations.get("running_right");
        } else {
            // If no movement keys are pressed, return the idle animation based on the last movement direction
            String lastDir = getLastDirection();
            if (lastDir != null) {
                return animations.get("idle_" + lastDir);
            } else {
                // Default to idle_down if no valid last movement direction is found
                return animations.get("idle_down");
            }
        }
    }

    public void dispose() {
        playerBatch.dispose();
        textureAtlas.dispose();
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


}
