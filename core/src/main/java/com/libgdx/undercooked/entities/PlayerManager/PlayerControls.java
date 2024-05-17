package com.libgdx.undercooked.entities.PlayerManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.libgdx.undercooked.entities.FoodType;
import com.libgdx.undercooked.entities.Station;
import com.libgdx.undercooked.entities.animLocker;

public class PlayerControls {

    Player player;
    public boolean isLifting;
    private float currentTime;
    private float deltaTimes;
    public animLocker animLock;
    public float playerLock;

    PlayerAnimations playerAnimations;

    public PlayerControls(Player player,PlayerAnimations playerAnimations){
        this.player = player;
        this.playerAnimations = playerAnimations;
    }


    public void inputUpdate(float deltaTime) {
        float horizontalForce = 0;
        float verticalForce = 0;
        currentTime += deltaTime;
        deltaTimes = deltaTime;
        playerAnimations.updateStateTime(deltaTime); // Increment stateTime here
        player.timeUpdate();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Station st = player.stationList.pointStation(player.getInteractPos());
            FoodType oft = player.getHeldItem();
            if (st != null) {
                if (st.interact(player)) {
                    if (st instanceof animLocker) {
                        ((animLocker) st).lockPlayer();
                        animLock = ((animLocker) st);

                    }
                    playerLock = 1f;
                    currentTime = 0;
                    if (oft != player.getHeldItem()) {
                        player.poofFrames = 1f;
                        isLifting = true;
                    }
                } else {
                    // invalid thing

                }
            } else {
                System.out.println("pointed at nothing");
                Gdx.input.setCursorPosition((int) player.getInteractPos().x * 32, (int) player.getInteractPos().y * 32);
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
            horizontalForce *= MathUtils.sin(MathUtils.PI2 / 8);
            verticalForce *= MathUtils.sin(MathUtils.PI2 / 8);
        }

        player.setLinearVelocity(horizontalForce * 5, verticalForce * 5);
    }

    public Animation<TextureRegion> determineCurrentAnimation() {
        String lastDir = player.getLastDirection();
        float animationSpeed = playerAnimations.getAnimation("lifting_" + lastDir).getAnimationDuration();
        currentTime += deltaTimes * animationSpeed;

        if (isLifting) {
            Animation<TextureRegion> liftingAnimation = playerAnimations.getAnimation("lifting_" + lastDir);
            if (currentTime >= liftingAnimation.getAnimationDuration() * 0.8f) {
                if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                    player.setLastDirection("top");
                    return playerAnimations.getAnimation("running_lifting_top");
                } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                    player.setLastDirection("left");
                    return playerAnimations.getAnimation("running_lifting_left");
                } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                    player.setLastDirection("down");
                    return playerAnimations.getAnimation("running_lifting_down");
                } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    player.setLastDirection("right");
                    return playerAnimations.getAnimation("running_lifting_right");
                } else {
                    return playerAnimations.getAnimation("idle_lifting_" + lastDir);
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
                player.setLastDirection("top");
                if (isLifting) return playerAnimations.getAnimation("running_lifting_top");
                return playerAnimations.getAnimation("running_top");
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                player.setLastDirection("left");
                if (isLifting) return playerAnimations.getAnimation("running_lifting_left");
                return playerAnimations.getAnimation("running_left");
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                player.setLastDirection("down");
                if (isLifting) return playerAnimations.getAnimation("running_lifting_down");
                return playerAnimations.getAnimation("running_down");
            } else {
                player.setLastDirection("right");
                if (isLifting) return playerAnimations.getAnimation("running_lifting_right");
                return playerAnimations.getAnimation("running_right");
            }
        } else {
            if (isLifting) return playerAnimations.getAnimation("idle_lifting_" + lastDir);
            return playerAnimations.getAnimation("idle_" + lastDir);
        }
    }

    private void debugKeys() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            if (player.hasHeldItem()) {
                System.out.println("Removing: " + player.getHeldItem());
                playerAnimations.setAnimationPlaying(true);
                playerAnimations.resetStateTime();
                player.poofFrames = 1f;
                player.shouldRemoveHeldItemAfterAnimation = true;
                player.removeHeldItem();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            System.out.println("Item check: " + player.getHeldItem());
        }
    }

    public Vector2 getPosition() {
        return player.getPosition();
    }
}
