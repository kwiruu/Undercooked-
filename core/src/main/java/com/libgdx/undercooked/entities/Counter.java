package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

import static com.libgdx.undercooked.GameManager.checkEntry;
import static com.libgdx.undercooked.GameManager.score;

public class Counter extends Station {
    Orders orders;
    public Counter(World world, float x, float y, int width, int height, SpriteBatch batch, Orders orders) {
        super(world, x, y, width, height, batch);
        this.orders = orders;
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
    public boolean interact(Player p) {
        System.out.println("interacted with counter");
        if (validate(p.getHeldItem())) {
            for (Orders.FoodOrder f : orders.getOrderList()) {
                if (p.getHeldItem() == f.getFoodType() && f.getActive()){
                    orders.rewardPoints(p.getHeldItem());
                    f.setInactive();
                    p.removeHeldItem();
                    checkEntry = true;
                }
            }
            // counter edit
            return true;
        }
        return false;
    }
    @Override
    public String toString() {
        return "Counter";
    }

    private boolean validate(FoodType ft) {
        if (ft == null) return false;
        for (Orders.FoodOrder f : orders.getOrderList()) {
            if (ft == f.getFoodType() && f.getActive()){
                return true;
            }
        }
        return false;
    }
}
