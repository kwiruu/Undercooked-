package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.libgdx.undercooked.PlayerManager;

public class ChoppingBoard extends Station implements canUpdate {
    int timer;
    boolean playerOn;

    public ChoppingBoard(float x, float y, int width, int height, SpriteBatch batch) {
        super(x, y, width, height, batch);
        // different classes different icons!
        floatingIconFrames = floating_iconAtlas.findRegions("chop_icon"); // Assuming "clock_icon" is the name of the animation
    }
    public void render() {
        // Update stateTime
        stateTime += (float) (Gdx.graphics.getDeltaTime() + .2);
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public void interact(PlayerManager p) {
        System.out.println("interacted with chopping board");
        if (timer == 0 && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                playerOn = true;
                timer = 500;
                p.setHeldItem(transmute(p.getHeldItem()));
                // trap player here
            } else {
                // show invalid sign
                System.out.println("invalid");
            }
        } else if (containedItem != null && !p.hasHeldItem()) {
            playerOn = true;
        } else {
            // show invalid sign
            System.out.println("invalid");
        }
    }

    private boolean validate(FoodType ft) {
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

    public void stopChopping() {
        playerOn = false;
    }
    @Override
    public void update() {
        if (playerOn && timer > 0) timer--;
    }
}
