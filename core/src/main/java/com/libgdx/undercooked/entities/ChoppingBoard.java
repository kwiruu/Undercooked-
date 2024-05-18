package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.entities.PlayerManager.PlayerControls;

public class ChoppingBoard extends Station implements canUpdate, animLocker {
    int timer;
    boolean playerOn;
    Player pon;
    public ChoppingBoard(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        // different classes different icons!
        floatingIconFrames = floating_iconAtlas.findRegions("chop_icon"); // Assuming "clock_icon" is the name of the animation
    }
    public void render() {
        // Update stateTime
        if (!playerOn) {
            stateTime += (float) (Gdx.graphics.getDeltaTime() + .2);
            TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
            batch.draw(currentFrame, getX(), getY());
        }

    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with chopping board");
        if (timer == 0 && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                playerOn = true;
                timer = 200;
                containedItem = transmute(p.getHeldItem());
                p.removeHeldItem();
                pon = p;
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
    public void update() {
        if (playerOn) {
            if (timer > 0) {
                timer--;
                System.out.println(timer);
            } else {
                pon.setHeldItem(containedItem);
                pon.removeAnimLocker();
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
