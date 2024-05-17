package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class Stove extends Station implements canUpdate {
    int timer;
    int max_timer;
    // spriteBatch is used to add a TextureRegion to a certain batch./
    // then the spriteBatch is then rendered!
    public Stove(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
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
    public void interact(Player p) {
        System.out.println("interacted with stove");
        if (containedItem == null && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                p.setHeldItem(transmute(p.getHeldItem()));
                timer = 500;
                max_timer = 500;
            } else {
                // show invalid sign
                System.out.println("invalid");
            }
        } else if (containedItem != null && timer == 0 && max_timer != 0 && !p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            containedItem = null;
            max_timer = 0;
        } else {
            // show invalid sign
            System.out.println("invalid");
        }
    }

    @Override
    public String toString() {
        return "Stove";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return false;
        switch (ft) {
            case meat:
            case fish:
                return true;
        }
        return false;
    }
    private FoodType transmute(FoodType ft) {
        switch (ft) {
            case meat:
                return FoodType.chopped_meat;
            case fish:
                return FoodType.chopped_fish;
        }
        return null;
    }

    @Override
    public void update() {
        if (timer > 0) timer--;
    }
}
