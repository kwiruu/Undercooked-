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
        super(x, y, width, height);
        this.batch = batch;
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
            // case 1 no item, p item
                // set cooking timer based on foodItem
                // need to check if valid foodItem
            if (p.getHeldItem() == FoodType.tomato && p.getHeldItem() == FoodType.chopped_tomato) {
                containedItem = FoodType.tomato_soup;
                timer = 500;
                p.removeHeldItem();
            }
        } else if (timer == 0 && !p.hasHeldItem()) {
            // case 2 done cooking, no p item
            p.setHeldItem(containedItem);
            containedItem = null;
        }
        // case 3-5 - do nothing
        // cooking
        // no item, no p item
        // done cooking, p item
    }

    // TODO station to continue needs implementations
}
