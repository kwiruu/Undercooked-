package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.libgdx.undercooked.PlayerManager;

public class FoodSource extends Station {

    private SpriteBatch batch;
    public FoodSource(float x, float y, int width, int height, SpriteBatch batch, FoodType foodType) {
        super(x, y, width, height);
        containedItem = foodType;
        this.batch = batch;

        // different classes different icons!
        // this else statement is para sa icon name depending on the foodtype!
        if(foodType.equals(FoodType.tomato)){
            floatingIconFrames = floating_iconAtlas.findRegions("tomato_icon");
        }else if(foodType.equals(FoodType.onion)){
            floatingIconFrames = floating_iconAtlas.findRegions("onion_icon");
        }else if(foodType.equals(FoodType.meat)){
            floatingIconFrames = floating_iconAtlas.findRegions("meat_icon");
        }else if(foodType.equals(FoodType.fish)){
            floatingIconFrames = floating_iconAtlas.findRegions("fish_icon");
        }else if(foodType.equals(FoodType.pickle)){
            floatingIconFrames = floating_iconAtlas.findRegions("pickle_icon");
        }

    }
    public void render() {
        // Update stateTime
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime += 0.5;
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }
    // TODO station to continue
    // when containedItem is taken, generate a new one

    /* TODO :
        1. to change the type of icon the food source make we need to find it in the region,
         so just pass the string name of the region for the foodsource to render for!
        2. foodSource object naming must be different on tiled since per map we need at most 3 foodsource!

    */

    @Override
    public void interact(PlayerManager p) {
        // case 1 no p item
        // set player item to contained item
        // case 2
        // do nothing
        if (!p.hasHeldItem()) {
            p.setHeldItem(containedItem);
        }
    }
}
