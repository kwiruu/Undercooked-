package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.libgdx.undercooked.PlayerManager;

public class FoodSource extends Station {
    public FoodSource(float x, float y, int width, int height, SpriteBatch batch, FoodType foodType) {
        super(x, y, width, height, batch);
        containedItem = foodType;
        switch(foodType) {
            case tomato:
                floatingIconFrames = floating_iconAtlas.findRegions("tomato_icon");
                break;
            case meat:
                floatingIconFrames = floating_iconAtlas.findRegions("meat_icon");
                break;
            case fish:
                floatingIconFrames = floating_iconAtlas.findRegions("fish_icon");
                break;
            case pickle:
                floatingIconFrames = floating_iconAtlas.findRegions("pickle_icon");
                break;
        }
    }
    public void render() {
        // Update stateTime
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime += 0.2;
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }
    /* TODO :
        1. to change the type of icon the food source make we need to find it in the region,
         so just pass the string name of the region for the foodsource to render for!
        2. foodSource object naming must be different on tiled since per map we need at most 3 foodsource!
    */
    @Override
    public void interact(PlayerManager p) {
        System.out.println("interacted with a foodSource for - " + containedItem);
        if (!p.hasHeldItem()) {
            p.setHeldItem(containedItem);
        }
    }
}
