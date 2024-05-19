package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class Stove extends Station implements canUpdate {
    int timer;
    int max_timer;
    public Stove(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("clock_icon_blank"); // idle
        floatingIconFrames[1] = floating_iconAtlas.findRegions("clock_icon"); // cooking
        floatingIconFrames[2] = floating_iconAtlas.findRegions("clock_icon_done"); // meal on standby
    }
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (timer > 0) {
            currentFrame = floatingIconFrames[1].get((int) (stateTime / frameDuration) % floatingIconFrames[1].size);
        } else if (containedItem != null) {
            currentFrame = floatingIconFrames[2].get((int) (stateTime / frameDuration) % floatingIconFrames[2].size);
        } else {
            currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        }
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with stove");
        if (containedItem == null && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                containedItem = transmute(p.getHeldItem());
                p.removeHeldItem();
                timer = 500;
                max_timer = 500;
                return true;
            }
        } else if (containedItem != null && timer == 0 && max_timer != 0 && !p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            containedItem = null;
            max_timer = 0;
            return true;
        }
        return false;
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
                return FoodType.cooked_meat;
            case fish:
                return FoodType.cooked_fish;
        }
        return null;
    }

    @Override
    public void update() {
        if (timer > 0) timer--;
    }
}
