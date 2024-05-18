package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class RiceCooker extends Station implements canUpdate {
    int timer;

    public RiceCooker(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        containedItem = FoodType.rice;
        floatingIconFrames = floating_iconAtlas.findRegions("clock_icon"); // Assuming "clock_icon" is the name of the animation
    }
    public void render() {
        // Update stateTime
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = floatingIconFrames.get((int) (stateTime / frameDuration) % floatingIconFrames.size);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with rice cooker");
        if (timer == 0 && p.hasHeldItem()) {
            p.setHeldItem(FoodType.rice);
            timer = 500;
            return true;
        } else if (timer == 0 && validate(p.getHeldItem())) {
            p.setHeldItem(transmute(p.getHeldItem()));
            timer = 500;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Rice Cooker";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return true;
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
        if (ft == null) return FoodType.rice;
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
