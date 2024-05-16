package com.libgdx.undercooked;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.entities.EntityList;
import com.libgdx.undercooked.entities.FoodType;
import com.libgdx.undercooked.entities.Station;
import com.libgdx.undercooked.entities.animLocker;

import java.util.HashMap;
import java.util.Map;
import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class PlayerManager implements Runnable {
    private final World world;
    static Body player;
    static Body itemBox;
    private TextureAtlas textureAtlas;
    private static SpriteBatch playerBatch;
    private String lastDirection;
    private Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    public boolean isLifting;
    private float currentTime;
    private float deltaTimes;
    public float playerLock;
    private float poofFrames;
    private animLocker animLock;
    private FoodType heldItem;
    private EntityList entityList;
    private Animation<TextureAtlas.AtlasRegion> smokeAnimation;
    private float stateTime;

    public static int x;
    public static int y;
    private boolean isSmokeAnimationPlaying = false;
    private boolean shouldRemoveHeldItemAfterAnimation;

    @Override
    public void run() {
        textureAtlas = new TextureAtlas(Gdx.files.internal("assets/sprites/Chef1Atlas.atlas"));
        setLocation();
        player = createBox(world, x, y, 16, 8, false);
        itemBox = createBox(world, 8, 10, 16, 16, false);
        playerBatch = new SpriteBatch();
        lastDirection = "down";
        animations = new HashMap<>();
        initializeAnimations();
        initializeSmokeAnimation();
        isLifting = false;
        currentTime = 0;
        stateTime = 0;
    }

    public void setLocation() {
        String selectedMap = getSelectedMap();
        switch (selectedMap) {
            case "Map1":
                PlayerManager.x = 8;
                PlayerManager.y = 2;
                break;
            case "Map2":
                PlayerManager.x = 18;
                PlayerManager.y = 12;
                break;
        }
    }

    public PlayerManager(World world) {
        this.world = world;
    }

    public SpriteBatch getBatch() {
        return playerBatch;
    }

    private void timeUpdate() {
        poofFrames-=.02f;
        if (playerLock > 0) {
            playerLock -= .03f;
        } else {
            if (heldItem == null) {
                isLifting = false;
            }
        }
    }
    public void inputUpdate(float deltaTime) {
        float horizontalForce = 0;
        float verticalForce = 0;
        currentTime += deltaTime;
        deltaTimes = deltaTime;
        stateTime += deltaTime; // Increment stateTime here
        timeUpdate();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Station st = entityList.pointStation(getInteractPos());
            FoodType oft = heldItem;
            if (st != null) {
                st.interact(this);
                if (st instanceof animLocker) {
                    ((animLocker) st).lockPlayer();
                    animLock = ((animLocker) st);
                }
                playerLock = 1f;
                currentTime = 0;
                if (oft != heldItem){
                    poofFrames = 1f;
                    isLifting = true;
                }
            } else {
                System.out.println("pointed at nothing");
                Gdx.input.setCursorPosition((int) getInteractPos().x * 32, (int) getInteractPos().y * 32);
            }
        }
        debugKeys();


        if (Gdx.input.isKeyPressed(Input.Keys.W) && playerLock <= 0) {
            verticalForce += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) && playerLock <= 0) {
            horizontalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) && playerLock <= 0) {
            verticalForce -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) && playerLock <= 0) {
            horizontalForce += 1;
        }
        if ((horizontalForce != 0 && verticalForce != 0) && playerLock <= 0) {
            verticalForce *= 0.7F;
            horizontalForce *= 0.7F;
        }
        player.setLinearVelocity(horizontalForce * 5, verticalForce * 5);
    }

    private Body createBox(World world, int x, int y, int width, int height, boolean isStatic) {
        Body pBody;
        BodyDef def = new BodyDef();

        if (isStatic) def.type = BodyDef.BodyType.StaticBody;
        else def.type = BodyDef.BodyType.DynamicBody;

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
                } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    setLastDirection("right");
                    return animations.get("running_lifting_right");
                } else {
                    return animations.get("idle_lifting_" + lastDir);
                }
            } else {
                return liftingAnimation;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) ||
            Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.D)) {

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                setLastDirection("top");
                if (isLifting) return animations.get("running_lifting_top");
                return animations.get("running_top");
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                setLastDirection("left");
                if (isLifting) return animations.get("running_lifting_left");
                return animations.get("running_left");
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                setLastDirection("down");
                if (isLifting) return animations.get("running_lifting_down");
                return animations.get("running_down");
            } else {
                setLastDirection("right");
                if (isLifting) return animations.get("running_lifting_right");
                return animations.get("running_right");
            }
        } else {
            // If no movement keys are pressed, return the corresponding idle animation
            if (isLifting) return animations.get("idle_lifting_" + lastDir);
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
    }

    public Vector2 getInteractPos() {
        // find station position
        Vector2 point = new Vector2(getPosition().x * 32, getPosition().y * 34);
        float displacement = 32;
        switch (lastDirection) {
            case "top":
            case "down":
                point.add(0, displacement);
                break;
            case "left":
                point.add(-displacement, 0);
                break;
            case "right":
                point.add(displacement, 0);
                break;
        }
        System.out.println(point);
        return point;
    }

    private void debugKeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (hasHeldItem()) {
                System.out.println("Removing: " + heldItem);
                isSmokeAnimationPlaying = true;
                stateTime = 0;
                poofFrames = 1f;
                shouldRemoveHeldItemAfterAnimation = true; // Set flag to remove item after animation
                removeHeldItem();
            }
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

    public String getItemName() {
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

    private void initializeSmokeAnimation() {
        TextureAtlas smokeAtlas = new TextureAtlas(Gdx.files.internal("assets/fx_sprites/fxAtlas.atlas"));
        Array<TextureAtlas.AtlasRegion> smokeRegions = smokeAtlas.findRegions("poof");
        smokeAnimation = new Animation<>(0.15f, smokeRegions);
    }

    private void renderPoofAnimation(SpriteBatch batch, float itemX, float itemY) {
        if (isSmokeAnimationPlaying) {
            TextureRegion currentFrame = smokeAnimation.getKeyFrame(stateTime, false); // false to stop looping
            batch.draw(currentFrame, itemX - 8, itemY - 8, 48, 48);

            if (smokeAnimation.isAnimationFinished(stateTime)) {
                isSmokeAnimationPlaying = false;
            }
        }
    }

    public void renderItem(SpriteBatch batch) {
        float itemX = (player.getPosition().x - 0.5f) * PPM;
        float amplitude = 0.08f;
        float frequency = 4.0f;
        float offsetY = amplitude * MathUtils.sin(frequency * stateTime);
        float itemY = (player.getPosition().y + 0.9f + offsetY) * PPM;

        // igo rani mu animate sa up and down
        if (heldItem != null) {
            Texture texture = new Texture(Gdx.files.internal("assets/food_sprites/raw_sprites/" + heldItem + ".png"));
            if (!texture.getTextureData().isPrepared()) {
                texture.getTextureData().prepare();
            }
            if (itemX >= 0 && itemX <= Gdx.graphics.getWidth() && itemY >= 0 && itemY <= Gdx.graphics.getHeight()) {
                batch.draw(texture, itemX, itemY);
            } else {
                Gdx.app.log("Warning", "Texture outside viewport");
            }
        }

        if (poofFrames > 0) {
            renderPoofAnimation(batch, itemX, itemY);
        }
    }

    public void renderItemUpdate(float elapsedTime) {
        if (isSmokeAnimationPlaying) {
            stateTime += elapsedTime;
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) || Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            stateTime = 0;  // Reset state time to start the animation from the beginning
            isSmokeAnimationPlaying = true;
        }
    }
}

