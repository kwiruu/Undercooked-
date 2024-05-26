package com.libgdx.undercooked.entities.Stations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.AudioManager.GameSound;
import com.libgdx.undercooked.entities.FoodType;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class Stove extends Station implements canUpdate {
    float timer;
    int max_timer;
    private int dispY = 0;
    GameSound gameSound = new GameSound();
    public Stove(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("clock_icon_blank"); // idle
        floatingIconFrames[1] = floating_iconAtlas.findRegions("clock_icon"); // cooking
        floatingIconFrames[2] = floating_iconAtlas.findRegions("clock_icon_done"); // meal on standby
    }
    public Stove(World world, float x, float y, int width, int height, SpriteBatch batch, int dispY) {
        super(world, x, y, width, height, batch);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("clock_icon_blank"); // idle
        floatingIconFrames[1] = floating_iconAtlas.findRegions("clock_icon"); // cooking
        floatingIconFrames[2] = floating_iconAtlas.findRegions("clock_icon_done"); // meal on standby
        this.dispY = dispY;
    }
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;
        if (timer > 0) {
            currentFrame = floatingIconFrames[1].get((int) (((1f - (timer/max_timer)) * floatingIconFrames[1].size) % floatingIconFrames[1].size));
        } else if (containedItem != null) {
            stateTime += 0.2f;
            currentFrame = floatingIconFrames[2].get((int) (stateTime / frameDuration) % floatingIconFrames[2].size);
        } else {
            currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        }
        batch.draw(currentFrame, getX(), getY() + dispY);
    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        if (containedItem == null && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                containedItem = transmute(p.getHeldItem());
                p.removeHeldItem();
                timer = 10;
                max_timer = 10;
                gameSound.startCookingSound();
                return true;
            }
        } else if (containedItem != null && timer <= 0 && max_timer != 0 && !p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            gameSound.startPoofSound();
            containedItem = null;
            max_timer = 0;
            return true;
        }
        gameSound.startErrorSound();
        return false;
    }

    @Override
    public String toString() {
        return "Stove";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return false;
        switch (ft) {
            case tomato:
            case meat:
            case fish:
            case chopped_meat:
            case chopped_fish:
                return true;
        }
        return false;
    }
    private FoodType transmute(FoodType ft) {
        switch (ft) {
            case tomato:
                return FoodType.tomato_soup;
            case meat:
                return FoodType.cooked_meat;
            case fish:
                return FoodType.cooked_fish;
            case chopped_meat:
                return FoodType.cooked_chopped_meat;
            case chopped_fish:
                return FoodType.cooked_chopped_fish;
        }
        return null;
    }

    @Override
    public void update(float deltaTime) {
        if (timer > 0) timer-=deltaTime;
    }
}
