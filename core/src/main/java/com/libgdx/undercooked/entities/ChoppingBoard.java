package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class ChoppingBoard extends Station implements canUpdate, animLocker {
    float timer;
    int max_timer;
    boolean playerOn = false;
    FoodType tempFood;
    Player pon;
    public ChoppingBoard(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("chop_icon"); // idle
        floatingIconFrames[1] = floating_iconAtlas.findRegions("clock_icon"); // chopping
        floatingIconFrames[2] = floating_iconAtlas.findRegions("meat_icon");// unfinished chopping (show containedItem)
    }
    @Override
    public void render() {
        // stateTime += (float) (Gdx.graphics.getDeltaTime() + .2);
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;

        // TODO this
        // it runs thrice for some reason
        // System.out.println("playerOn = " + playerOn + ", containedItem = " + containedItem);
        if (!playerOn && timer <= 0){
            //System.out.println("idle");
            currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        } else if (playerOn) {
            //System.out.println("chopping");
            currentFrame = floatingIconFrames[1].get((int) (stateTime / frameDuration) % floatingIconFrames[1].size);
        } else {
            //System.out.println("cut progress");
            if(tempFood != null){
            floatingIconFrames[2] = floating_iconAtlas.findRegions(tempFood.toString() + "_icon");
            }
            currentFrame = floatingIconFrames[2].get((int) (stateTime / frameDuration) % floatingIconFrames[2].size);

        }
        batch.draw(currentFrame, getX(), getY());

    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with chopping board");
        if (timer == 0 && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                playerOn = true;
                max_timer = 200;
                timer = 200;
                tempFood = p.getHeldItem();
                containedItem = transmute(p.getHeldItem());
                p.removeHeldItem();
                pon = p;
                // set this to current containedItem
                // floatingIconFrames2 = floating_iconAtlas.findRegions("chop_icon");
                return true;
            }
        } else if (containedItem != null && !p.hasHeldItem()) {
            playerOn = true;
            pon = p;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Chopping Board";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return false;
        switch (ft) {
            case tomato:
            case onion:
            case pickle:
            case meat:
            case fish:
                return true;
        }
        return false;
    }
    private FoodType transmute(FoodType ft) {
        switch (ft) {
            case tomato:
                return FoodType.chopped_tomato;
            case onion:
                return FoodType.chopped_onion;
            case pickle:
                return FoodType.chopped_pickle;
            case meat:
                return FoodType.chopped_meat;
            case fish:
                return FoodType.chopped_fish;
        }
        return null;
    }

    @Override
    public void update(float deltaTime) {
        if (playerOn) {
            if (timer > 0) {
                timer-=deltaTime;
            } else {
                max_timer = 0;
                pon.setHeldItem(containedItem);
                pon.removeAnimLocker();
                pon.outPoof();
                containedItem = null;
                exitPlayer();
            }
        }
    }

    @Override
    public void exitPlayer() {
        pon = null;
        playerOn = false;
    }
}
