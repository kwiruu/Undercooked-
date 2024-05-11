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
        stateTime += (float) (Gdx.graphics.getDeltaTime() + .5);
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public void interact(PlayerManager p) {
        System.out.println("chopping board");
        if (containedItem == null && p.hasHeldItem()) {
            // case 1 no item, p item
                // set chopping timer based on foodItem
                // need to check if valid foodItem
            if (p.getHeldItem() == FoodType.tomato && p.getHeldItem() == FoodType.chopped_tomato) {
                containedItem = FoodType.chopped_tomato;
                timer = 500;
                p.removeHeldItem();
            }
        } else if (timer == 0 && !p.hasHeldItem()) {
            // case 2 has item, any p item
                // continue chopping
            // filler code
            timer+=1;
        }
        // case 3-5 - do nothing
        // cooking
        // no item, no p item
        // done cooking, p item
    }
    // TODO station to continue
}
