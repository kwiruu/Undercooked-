package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class FoodSource extends Station {
    public FoodSource(World world, float x, float y, int width, int height, SpriteBatch batch, FoodType foodType) {
        super(world, x, y, width, height, batch);
        containedItem = foodType;
        // different classes different icons!
        // this else statement is para sa icon name depending on the foodtype!
        switch(foodType) {
            case tomato:
                floatingIconFrames[0] = floating_iconAtlas.findRegions("tomato_icon");
                break;
            case meat:
                floatingIconFrames[0] = floating_iconAtlas.findRegions("meat_icon");
                break;
            case fish:
                floatingIconFrames[0] = floating_iconAtlas.findRegions("fish_icon");
                break;
            case pickle:
                floatingIconFrames[0] = floating_iconAtlas.findRegions("pickle_icon");
                break;
            default:
                floatingIconFrames[0] = floating_iconAtlas.findRegions("pickle_icon");
        }
//        if (foodType.equals(FoodType.tomato)) {
//            floatingIconFrames[0] = floating_iconAtlas.findRegions("tomato_icon");
//        } else if (foodType.equals(FoodType.onion)) {
//            floatingIconFrames[0] = floating_iconAtlas.findRegions("onion_icon");
//        } else if (foodType.equals(FoodType.meat)) {
//            floatingIconFrames[0] = floating_iconAtlas.findRegions("meat_icon");
//        } else if (foodType.equals(FoodType.fish)) {
//            floatingIconFrames[0] = floating_iconAtlas.findRegions("fish_icon");
//        } else if (foodType.equals(FoodType.pickle)) {
//            floatingIconFrames[0] = floating_iconAtlas.findRegions("pickle_icon");
//        }
    }
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime += 0.2f;
        TextureRegion currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        batch.draw(currentFrame, getX(), getY());
    }
    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a foodSource for - " + containedItem);
        if (!p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Food Source";
    }
}
