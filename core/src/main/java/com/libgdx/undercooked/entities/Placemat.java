package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class Placemat extends Station {
    public Placemat(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
    }

    @Override
    public void render() {

    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a placeMat for - " + containedItem);
        if (containedItem == null && p.hasHeldItem()) {
            containedItem = p.getHeldItem();
            p.removeHeldItem();
            return true;
        } else if (containedItem != null && !p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            containedItem = null;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Placemat";
    }
}
