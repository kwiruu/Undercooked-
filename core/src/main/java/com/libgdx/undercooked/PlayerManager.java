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

public class PlayerManager implements Runnable{
    static Body player;
    private TextureAtlas textureAtlas;
    private SpriteBatch playerBatch;
    private final World world;
    private String lastDirection;
    private final Map<String, Animation<TextureRegion>> animations = new HashMap<>();;
    public boolean isLifting;
    private float currentTime;
    private boolean spacePressed = false;
    private float spaceCooldown = 1f;

    @Override
    public void run() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("assets/sprites/Chef1Atlas.atlas"));
        player = createBox(world, 8, 2, 16, 8, false);
        playerBatch = new SpriteBatch();
        lastDirection = "down";
        initializeAnimations();
        isLifting = false;
        currentTime = 0;
    }

    public PlayerManager(World world) {
        this.world = world;

    }

    public SpriteBatch getBatch() {
        return playerBatch;
    }

    public void inputUpdate(float deltaTime) {
        float horizontalForce = 0;
        float verticalForce = 0;
        currentTime += deltaTime;

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !spacePressed) {
            spacePressed = true;
            isLifting = !isLifting; // Toggle lifting state
            currentTime = 0; // Reset animation time
        }

        // Update space cooldown
        if (spacePressed) {
            spaceCooldown -= deltaTime;
            if (spaceCooldown <= 0) {
                spacePressed = false;
                spaceCooldown = 1f; // Reset cooldown
            }
        }

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

    Animation<TextureRegion> determineCurrentAnimation() {
        String lastDir = getLastDirection();

        if (isLifting) {
            Animation<TextureRegion> liftingAnimation = animations.get("lifting_" + lastDir);
            // Check if lifting animation is close to finishing based on a threshold
            if (currentTime >= liftingAnimation.getAnimationDuration() * 0.9f) {
                    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                        setLastDirection("top");
                            return animations.get("running_lifting_top");
                    } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                        setLastDirection("left");
                            return animations.get("running_lifting_left");
                    } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                        setLastDirection("down");
                            return animations.get("running_lifting_down");
                    } else if (Gdx.input.isKeyPressed(Input.Keys.D)){
                    setLastDirection("right");
                    return animations.get("running_lifting_right");
                    } else{
                    return animations.get("idle_lifting_" + lastDir);
                }
            } else {
                return liftingAnimation;
            }
        }

        // Check if any movement keys are pressed
        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
            Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.D)) {

            // If movement keys are pressed, return the corresponding running animation
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                setLastDirection("top");
                if (isLifting) {
                    return animations.get("running_lifting_top");
                }
                return animations.get("running_top");
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                setLastDirection("left");
                if (isLifting) {
                    return animations.get("running_lifting_left");
                }
                return animations.get("running_left");
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                setLastDirection("down");
                if (isLifting) {
                    return animations.get("running_lifting_down");
                }
                return animations.get("running_down");
            } else if(Gdx.input.isKeyPressed(Input.Keys.D)){
                setLastDirection("right");
                if (isLifting) {
                    return animations.get("running_lifting_right");
                }
                return animations.get("running_right");
            }
        } else {
            // If no movement keys are pressed, return the corresponding idle animation
            if (isLifting) {
                return animations.get("idle_lifting_" + lastDir);
            }
        }
        return animations.get("idle_" + lastDir);
    }

    public boolean isAnimationFinished(){
        return animations.get("lifting_"+getLastDirection()).isAnimationFinished(currentTime);
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

    private void initializeAnimations() {
        // running anim
        animations.put("running_down", new Animation<>(0.09f,  textureAtlas.findRegions("running_down")));
        animations.put("running_top", new Animation<>(0.09f, textureAtlas.findRegions("running_top")));
        animations.put("running_left", new Animation<>(0.09f, textureAtlas.findRegions("running_left")));
        animations.put("running_right", new Animation<>(0.09f, textureAtlas.findRegions("running_right")));
        // idle anim
        animations.put("idle_down", new Animation<>(0.09f, textureAtlas.findRegions("idle_down")));
        animations.put("idle_top", new Animation<>(0.09f, textureAtlas.findRegions("idle_up")));
        animations.put("idle_left", new Animation<>(0.09f, textureAtlas.findRegions("idle_left")));
        animations.put("idle_right", new Animation<>(0.09f, textureAtlas.findRegions("idle_right")));
        // lifting anim
        animations.put("lifting_down", new Animation<>(0.05f, textureAtlas.findRegions("lifting_down")));
        animations.put("lifting_top", new Animation<>(0.05f, textureAtlas.findRegions("lifting_top")));
        animations.put("lifting_left", new Animation<>(0.05f, textureAtlas.findRegions("lifting_left")));
        animations.put("lifting_right", new Animation<>(0.05f, textureAtlas.findRegions("lifting_right")));
        // lifting idle anim
        animations.put("idle_lifting_top", new Animation<>(0.09f, textureAtlas.findRegions("idle_lifting_top")));
        animations.put("idle_lifting_down", new Animation<>(0.12f, textureAtlas.findRegions("idle_lifting_down")));
        animations.put("idle_lifting_left", new Animation<>(0.12f, textureAtlas.findRegions("idle_lifting_left")));
        animations.put("idle_lifting_right", new Animation<>(0.12f, textureAtlas.findRegions("idle_lifting_right")));
        // lifting running anim
        animations.put("running_lifting_down", new Animation<>(0.12f, textureAtlas.findRegions("running_lifting_down")));
        animations.put("running_lifting_top", new Animation<>(0.09f, textureAtlas.findRegions("running_lifting_top")));
        animations.put("running_lifting_left", new Animation<>(0.12f, textureAtlas.findRegions("running_lifting_left")));
        animations.put("running_lifting_right", new Animation<>(0.12f, textureAtlas.findRegions("running_lifting_right")));
    }

}
