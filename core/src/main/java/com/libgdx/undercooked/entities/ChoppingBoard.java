package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.libgdx.undercooked.PlayerManager;

public class ChoppingBoard extends Station {
    private SpriteBatch batch;
    int timer;

    public ChoppingBoard(float x, float y, int width, int height, SpriteBatch batch) {
        super(x, y, width, height);
        this.batch = batch;
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
            // start chop
            if (validate(p.getHeldItem())) {
                timer = 500;
                transmute(p.getHeldItem());
                p.removeHeldItem();
                // trap player here
            }
        } else if (!p.hasHeldItem()) {
            // trap player and continue timer here
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
    private void transmute(FoodType ft) {
        switch (ft) {
            case tomato:
                ft = FoodType.chopped_tomato;
                return;
            case onion:
                ft = FoodType.chopped_onion;
                return;
            case pickle:
                ft = FoodType.chopped_pickle;
                return;
            case meat:
                ft = FoodType.chopped_meat;
                return;
            case fish:
                ft = FoodType.chopped_fish;
                return;
        }
    }
    // TODO station to continue
}
