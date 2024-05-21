package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import java.util.HashMap;

public class Placemat extends Station implements Disposable {
    private HashMap<FoodType, Texture> foodTextures;

    public Placemat(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        loadFoodTextures();
    }

    private void loadFoodTextures() {
        foodTextures = new HashMap<>();
        for (FoodType foodType : FoodType.values()) {
            String texturePath = "assets/food_sprites/foods/" + foodType + ".png";
            foodTextures.put(foodType, new Texture(texturePath));
        }
    }

    @Override
    public void render() {
        if (containedItem != null) {
            Texture texture = foodTextures.get(containedItem);
            if (texture != null) {
                batch.draw(texture, getX(), getY());
            }
        }
    }

    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        if (containedItem == null && p.hasHeldItem()) {
            containedItem = p.getHeldItem();
            p.removeHeldItem();
            return true;
        } else if (containedItem != null && !p.hasHeldItem()) {
            p.setHeldItem(containedItem);
            containedItem = null;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Placemat";
    }

    @Override
    public void dispose() {
        for (Texture texture : foodTextures.values()) {
            texture.dispose();
        }
    }
}
