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
//    float tempX;
//    float tempY;
    public boolean isLifting;
    private float currentTime;
    private float deltaTimes;
    public animLocker animLock;
    public float playerLock;
    public int invalidTimer;

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
        invalidTimer--;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (animLock == null) {
                Station st = player.stationList.pointStation(player.getInteractPos());
                FoodType oft = player.getHeldItem();
                isLifting = true;
                if (st != null) {
                    if (st.interact(player)) {
                        if (st instanceof animLocker) {
                            System.out.println("interacted with animLocker");
                            animLock = ((animLocker) st);
//                            tempX = getPosition().x;
//                            tempY = getPosition().y;
                        }
                        playerLock = 1f;
                        currentTime = 0;
                        if (oft != player.getHeldItem()) {
                            poof();
                        }
                    } else {
                        invalidTimer = 20;
                    }
                } else {
                    System.out.println("pointed at nothing");
                    Gdx.input.setCursorPosition((int) player.getInteractPos().x * 32, (int) player.getInteractPos().y * 32);
                }
            } else {
                animLock.exitPlayer();
                animLock = null;
//                getPosition().x = tempX;
//                getPosition().y = tempY;
            }
        }
        debugKeys();

        if (playerLock <= 0 && animLock == null) {
            int speed = 1;
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) speed = 2;
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                verticalForce += speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                horizontalForce -= speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                verticalForce -= speed;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                horizontalForce += speed;
            }
            if ((horizontalForce != 0 && verticalForce != 0)) {
                horizontalForce *= MathUtils.sin(MathUtils.PI2 / 8);
                verticalForce *= MathUtils.sin(MathUtils.PI2 / 8);
            }
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

        if ((Gdx.input.isKeyPressed(Input.Keys.W) ||
            Gdx.input.isKeyPressed(Input.Keys.A) ||
            Gdx.input.isKeyPressed(Input.Keys.S) ||
            Gdx.input.isKeyPressed(Input.Keys.D)) && animLock == null) {

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
                poof();
                player.shouldRemoveHeldItemAfterAnimation = true;
                player.removeHeldItem();
            }
        }
    }
    public void poof() {
        player.poofFrames = .8f;
    }
    public void poof2() {
        playerAnimations.setAnimationPlaying(true);
        playerAnimations.resetStateTime();
        poof();
        currentTime = 0;
        isLifting = true;
    }

    public Vector2 getPosition() {
        return player.getPosition();
    }
    public void removeAnimLocker() {
        animLock = null;
    }
}
