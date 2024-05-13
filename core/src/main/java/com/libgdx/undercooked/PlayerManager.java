package com.libgdx.undercooked;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.EntityList;
import com.libgdx.undercooked.entities.FoodType;
import com.libgdx.undercooked.entities.Station;

import java.util.HashMap;
import java.util.Map;

import static com.libgdx.undercooked.utils.Constants.PPM;

public class PlayerManager implements Runnable {
    private final World world;
    static Body player;
    static Body itemBox;
    static String hasItemz = "";
    private TextureAtlas textureAtlas;
    private SpriteBatch playerBatch;
    private String lastDirection;
    private Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    public boolean isLifting;
    private float currentTime;
    private boolean spacePressed = false;
    private float spaceCooldown = 1f;
    private float deltaTimes;
    public boolean playerLocked = false;
    private FoodType heldItem;
    private EntityList entityList;

    @Override
    public void run(){
        textureAtlas = new TextureAtlas(Gdx.files.internal("assets/sprites/Chef1Atlas.atlas"));
        player = createBox(world, 8, 2, 16, 8, false);
        itemBox = createBox(world,8,10,16,16,false);
        playerBatch = new SpriteBatch();
        lastDirection = "down";
        animations = new HashMap<>();
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
        deltaTimes = deltaTime;

        if(hasHeldItem()){
            hasItemz = getHeldItem() + "";
        }else{
            hasItemz = "";
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !spacePressed) {
            if(!playerLocked && !isLifting){
                playerLocked = true;
            }
            spacePressed = true;
            isLifting = !isLifting; // Toggle lifting state
            currentTime = 0; // Reset animation time
            Station st = entityList.pointStation(getInteractPos());
           // Station st = entityList.pointStation(debugInteractPos());
            if (st != null) {
                st.interact(this);
            } else {
                System.out.println("pointed at nothing");
                Gdx.input.setCursorPosition((int) getInteractPos().x, (int) getInteractPos().y);
            }
        }
        debugKeys();

        if (spacePressed) {
            spaceCooldown -= 0.03f;
            if (spaceCooldown <= 0) {
                spacePressed = false;
                if(playerLocked){
                    playerLocked = false;
                }
                spaceCooldown = 1f;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && !playerLocked) {
            verticalForce += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !playerLocked) {
            horizontalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && !playerLocked) {
            verticalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && !playerLocked) {
            horizontalForce += 1;
        }
        if ((horizontalForce != 0 && verticalForce != 0)&& !playerLocked) {
            verticalForce *= 0.7F;
            horizontalForce *= 0.7F;
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



    public Animation<TextureRegion> determineCurrentAnimation() {
        String lastDir = getLastDirection();

        float animationSpeed = animations.get("lifting_" + lastDir).getAnimationDuration();
        currentTime += deltaTimes * animationSpeed;

        if (isLifting) {
            Animation<TextureRegion> liftingAnimation = animations.get("lifting_" + lastDir);
            // Check if lifting animation is close to finishing based on a threshold
            if (currentTime >= liftingAnimation.getAnimationDuration() * 0.8f) {
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
            } else { // Gdx.input.isKeyPressed(Input.Keys.D)
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
            return animations.get("idle_" + lastDir);
        }
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
        animations.put("lifting_down", new Animation<>(0.12f, textureAtlas.findRegions("lifting_down")));
        animations.put("lifting_top", new Animation<>(0.12f, textureAtlas.findRegions("lifting_top")));
        animations.put("lifting_left", new Animation<>(0.12f, textureAtlas.findRegions("lifting_left")));
        animations.put("lifting_right", new Animation<>(0.12f, textureAtlas.findRegions("lifting_right")));
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
        //H

    }

    public Vector2 getInteractPos() {
        // find station position
        Vector2 point = new Vector2(getPosition().x * 32, getPosition().y * 34);
        float displacement = 32;
        switch (lastDirection) {
            case "top":
                point.add(0, displacement);
                break;
            case "down":
                point.add(0, displacement);
                break;
            case "left":
                point.add(-displacement,0);
                break;
            case "right":
                point.add(displacement,0);
                break;
        }
        System.out.println(point);
        return point;
    }
    private Vector2 debugInteractPos() {
        Vector2 point = new Vector2(545, 407);
        System.out.println(point);
        return point;
    }
    private void debugKeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            System.out.println("Removing: " + heldItem);
            isLifting = false;
            removeHeldItem();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            System.out.println("Item check: " + heldItem);
        }
    }
    public boolean hasHeldItem() {
        return getHeldItem() != null;
    }
    public FoodType getHeldItem() {
        return heldItem;
    }

    public String getItemName(){
        return heldItem + "";
    }
    public void removeHeldItem() {
        heldItem = null;
    }
    public void setHeldItem(FoodType heldItem) {
        this.heldItem = heldItem;
    }
    public void setEntityList(EntityList entityList) {
        this.entityList = entityList;
    }
}
