package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.PlayerManager;

import java.awt.Rectangle;

public class Stove extends Station {
    private SpriteBatch batch;
    int timer;

    // spriteBatch is used to add a TextureRegion to a certain batch./
    // then the spriteBatch is then rendered!
    public Stove(float x, float y, int width, int height, SpriteBatch batch) {
        super(x, y, width, height, batch);
        // different classes different icons!
        floatingIconFrames = floating_iconAtlas.findRegions("clock_icon"); // Assuming "clock_icon" is the name of the animation
    }
    public void render() {
        // Update stateTime
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public void interact(PlayerManager p) {
        System.out.println("interacted with stove");
        if (containedItem == null && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                timer = 500;
            }
        } else if (containedItem != null && timer == 0 && !p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            containedItem = null;
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
}
