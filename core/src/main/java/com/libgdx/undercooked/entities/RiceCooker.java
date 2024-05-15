package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.PlayerManager;

import java.awt.Rectangle;

public class RiceCooker extends Station implements canUpdate {
    int timer;

    public RiceCooker(float x, float y, int width, int height, SpriteBatch batch) {
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
        System.out.println("interacted with rice cooker");
        if (timer == 0 && p.hasHeldItem()) {
            p.setHeldItem(FoodType.rice);
            timer = 500;
        } else if (timer == 0 && validate(p.getHeldItem())) {
            p.setHeldItem(transmute(p.getHeldItem()));
            timer = 500;
        } else {
            // show invalid sign
            System.out.println("invalid");
        }
    }
    private boolean validate(FoodType ft) {
        switch (ft) {
            case cooked_meat:
            case cooked_fish:
            case chopped_tomato:
            case chopped_onion:
            case chopped_pickle:
                return true;
        }
        return false;
    }
    private FoodType transmute(FoodType ft) {
        switch (ft) {
            case cooked_meat:
                return FoodType.meat_meal;
            case cooked_fish:
                return FoodType.fish_meal;
            case chopped_tomato:
            case chopped_onion:
            case chopped_pickle:
                return FoodType.struggle_meal;
        }
        return null;
    }

    @Override
    public void update() {
        if (timer > 0) timer--;
    }
}
