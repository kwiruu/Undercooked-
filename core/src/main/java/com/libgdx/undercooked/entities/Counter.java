package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.GameManager;
import com.libgdx.undercooked.entities.PlayerManager.Player;


public class Counter extends Station {
    Orders orders;
    public Counter(World world, float x, float y, int width, int height, SpriteBatch batch, Orders orders) {
        super(world, x, y, width, height, batch);
        this.orders = orders;
        floatingIconFrames[0] = floating_iconAtlas.findRegions("counter_icon");
    }

    @Override
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime += 0.2f;
        TextureRegion currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        batch.draw(currentFrame, getX(), getY());
    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        if (validate(p.getHeldItem())) {
            for (Orders.FoodOrder f : orders.getOrderList()) {
                if (p.getHeldItem() == f.getFoodType() && f.isActive()){
                    orders.rewardPoints(p.getHeldItem());
                    f.setInactive();
                    p.removeHeldItem();
                    GameManager.setCheckEntry(true);
                }
            }
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
            if (ft == f.getFoodType() && f.isActive()){
                return true;
            }
        }
        return false;
    }
}
