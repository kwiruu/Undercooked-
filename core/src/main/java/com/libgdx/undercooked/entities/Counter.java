package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import PlayerManager.Player;

public class Counter extends Station {
    public Counter(float x, float y, int width, int height, SpriteBatch batch) {
        super(x, y, width, height, batch);
        // different classes different icons!
        floatingIconFrames = floating_iconAtlas.findRegions("clock_icon"); // Assuming "clock_icon" is the name of the animation
    }

    @Override
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public void interact(Player p) {
        System.out.println("interacted with stove");
        if (validate(p.getHeldItem())) {
            p.removeHeldItem();
            // counter edit
        }
    }



    @Override
    public String toString() {
        return "Counter";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return false;
        switch (ft) {
            case tomato:
            case pickle:
            case chopped_tomato:
            case chopped_pickle:
            case cooked_meat:
            case cooked_fish:
            case tomato_soup:
            case rice:
            case meat_meal:
            case fish_meal:
            case struggle_meal:
                return true;
        }
        return false;
    }
}
