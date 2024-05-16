package com.libgdx.undercooked.entities.PlayerManager;

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
import com.libgdx.undercooked.entities.EntityList;
import com.libgdx.undercooked.entities.FoodType;

import static com.libgdx.undercooked.screen.SelectionScreen.getSelectedMap;
import static com.libgdx.undercooked.utils.Constants.PPM;

public class Player implements Runnable {
    private final World world;

    public static Body player;
    private SpriteBatch playerBatch;
    private String lastDirection;
    private PlayerAnimations playerAnimations;
    float poofFrames;
    private FoodType heldItem;
    EntityList entityList;

    public static int x;

    public static int y;
    PlayerControls playerControls;
    boolean shouldRemoveHeldItemAfterAnimation;

    public Player(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("assets/sprites/Chef1Atlas.atlas"));
        TextureAtlas smokeAtlas = new TextureAtlas(Gdx.files.internal("assets/fx_sprites/fxAtlas.atlas"));
        playerAnimations = new PlayerAnimations(textureAtlas, smokeAtlas);
        playerControls = new PlayerControls(this,playerAnimations);
        setLocation();
        player = createBox(world, x, y, 16, 8, false);
        playerBatch = new SpriteBatch();
        lastDirection = "down";
    }

    public void setLocation() {
        String selectedMap = getSelectedMap();
        switch (selectedMap) {
            case "Map1":
                Player.x = 8;
                Player.y = 2;
                break;
            case "Map2":
                Player.x = 18;
                Player.y = 12;
                break;
        }
    }

    public SpriteBatch getBatch() {
        return playerBatch;
    }

    void timeUpdate() {
        poofFrames -= .02f;
        if (playerControls.playerLock > 0) {
            playerControls.playerLock -= .03f;
        } else {
            if (heldItem == null) {
                playerControls.isLifting = false;
            }
        }
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

    public Vector2 getInteractPos() {
        // find station position
        Vector2 point = new Vector2(playerControls.getPosition().x * 32, playerControls.getPosition().y * 34);
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

    public void setLastDirection(String direction) {
        this.lastDirection = direction;
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public Vector2 getPosition() {
        return player.getPosition();
    }
    public boolean hasHeldItem() {
        return getHeldItem() != null;
    }

    public FoodType getHeldItem() {
        return heldItem;
    }

    public SpriteBatch getPlayerBatch(){
        return playerBatch;
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

    private void renderPoofAnimation(SpriteBatch batch, float itemX, float itemY) {
        if (playerAnimations.isAnimationPlaying()) {
            TextureRegion currentFrame = playerAnimations.getSmokeAnimation().getKeyFrame(playerAnimations.getStateTime(), false); // false to stop looping
            batch.draw(currentFrame, itemX - 8, itemY - 8, 48, 48);

            if (playerAnimations.getSmokeAnimation().isAnimationFinished(playerAnimations.getStateTime())) {
                playerAnimations.setAnimationPlaying(false);
            }
        }
    }

    public void renderItem(SpriteBatch batch) {
        float itemX = (player.getPosition().x - 0.5f) * PPM;
        float amplitude = 0.08f;
        float offsetY = amplitude * MathUtils.sin(playerAnimations.getStateTime());
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
        if (playerAnimations.isAnimationPlaying()) {
            playerAnimations.updateStateTime(elapsedTime);
        }
        if ((Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ) || Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            playerAnimations.resetStateTime();
            playerAnimations.setAnimationPlaying(true);
        }
    }

    public void setLinearVelocity(float v, float v1) {
        player.setLinearVelocity(v,v1);
    }

    public void inputUpdate(float deltaTime) {
        playerControls.inputUpdate(deltaTime);
    }

    public Animation<TextureRegion> determineCurrentAnimation() {
        return playerControls.determineCurrentAnimation();
    }
}