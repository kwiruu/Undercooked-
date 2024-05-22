package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.AudioManager.CookingSound;
import com.libgdx.undercooked.AudioManager.GameSound;
import com.libgdx.undercooked.AudioManager.SlicingSound;
import com.libgdx.undercooked.entities.PlayerManager.Player;

import java.util.Objects;

public class ChoppingBoard extends Station implements canUpdate, animLocker {
    float timer;
    int max_timer;
    boolean playerOn = false;
    Player pon;
    GameSound gameSound = new GameSound();

    public ChoppingBoard(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("chop_icon"); // idle
        floatingIconFrames[1] = floating_iconAtlas.findRegions("clock_icon"); // chopping
        floatingIconFrames[2] = floating_iconAtlas.findRegions("question_icon");// unfinished chopping (show containedItem)
    }
    @Override
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame;

        if (playerOn && Objects.equals(pon.getLastDirection(), "down")) {
            batch.setColor(1, 1, 1, 0.5f);
        }
        if (!playerOn && timer <= 0){
            currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        } else if (playerOn) {
            currentFrame = floatingIconFrames[1].get((int) (((1f - (timer/max_timer)) * floatingIconFrames[1].size) % floatingIconFrames[1].size));
        } else {
            if(containedItem != null){
                floatingIconFrames[2] = floating_iconAtlas.findRegions(containedItem + "_icon");
            }
            stateTime += 0.2f;
            currentFrame = floatingIconFrames[2].get((int) (stateTime / frameDuration) % floatingIconFrames[2].size);

        }
        batch.draw(currentFrame, getX(), getY() + 25);
        batch.setColor(1, 1, 1, 1f);
    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        if (timer <= 0 && p.hasHeldItem()) {
            if (validate(p.getHeldItem())) {
                playerOn = true;
                gameSound.startSlicingSound();
                max_timer = 4;
                timer = 4;
                containedItem = p.getHeldItem();
                p.removeHeldItem();
                pon = p;
                floatingIconFrames[2] = floating_iconAtlas.findRegions(containedItem + "_icon");
                return true;
            }
        } else if (containedItem != null && !p.hasHeldItem()) {
            playerOn = true;
            pon = p;
            return true;
        }
        gameSound.startErrorSound();
        return false;
    }

    @Override
    public String toString() {
        return "ChoppingBoard";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return false;
        switch (ft) {
            case tomato:
            case onion:
            case pickle:
            case meat:
            case fish:
                return true;
        }
        return false;
    }
    private FoodType transmute(FoodType ft) {
        switch (ft) {
            case tomato:
                return FoodType.chopped_tomato;
            case onion:
                return FoodType.chopped_onion;
            case pickle:
                return FoodType.chopped_pickle;
            case meat:
                return FoodType.chopped_meat;
            case fish:
                return FoodType.chopped_fish;
        }
        return null;
    }

    @Override
    public void update(float deltaTime) {
        if (playerOn) {
            if (timer > 0) {
                timer-=deltaTime;
            } else {
                max_timer = 0;
                pon.setHeldItem(transmute(containedItem));
                pon.removeAnimLocker();
                pon.outPoof();
                containedItem = null;
                exitPlayer();
            }
        }
    }

    @Override
    public void exitPlayer() {
        pon = null;
        playerOn = false;
        floatingIconFrames[2] = floating_iconAtlas.findRegions(containedItem + "_icon");
    }
}
