package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import java.util.HashMap;

public class Placemat extends Station implements Disposable {
    private HashMap<FoodType, Texture> foodTextures;
    private int dispY = 0;
    public Placemat(World world, float x, float y, int width, int height, SpriteBatch batch) {
        super(world, x, y, width, height, batch);
        loadFoodTextures();
    }

    public Placemat(World world, float x, float y, int width, int height, SpriteBatch batch, int dispY) {
        super(world, x, y, width, height, batch);
        loadFoodTextures();
        this.dispY = dispY;
    }

    private void loadFoodTextures() {
        foodTextures = new HashMap<>();
        for (FoodType foodType : FoodType.values()) {
            // TODO implement missing food textures
            switch (foodType) {
                case cooked_chopped_meat:
                case cooked_chopped_fish:
                case meat_meal_tomato:
                case meat_meal_onion:
                case fish_meal_tomato:
                case fish_meal_onion:
                    continue;
            }
            String texturePath = "assets/food_sprites/foods/" + foodType + ".png";
            foodTextures.put(foodType, new Texture(texturePath));
        }
    }

    @Override
    public void render() {
        if (containedItem != null) {
            Texture texture = foodTextures.get(containedItem);
            if (texture != null) {
                switch (containedItem) {
                    case meat:
                        batch.draw(texture, getX(), getY() + dispY - 3);
                        break;
                    default:
                        batch.draw(texture, getX(), getY() + dispY);
                }
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
        } else if (containedItem != null && validate(p.getHeldItem())) {
            if (transmute(p.getHeldItem()) != null) {
                containedItem = transmute(p.getHeldItem());
                p.removeHeldItem();
                return true;
            }
        }
        return false;
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
                if (containedItem == FoodType.chopped_onion) {
                    return FoodType.cooked_meat_onion;
                } else if (containedItem == FoodType.chopped_tomato) {
                    return FoodType.cooked_meat_tomato;
                }
                break;
            case cooked_fish:
                if (containedItem == FoodType.chopped_onion) {
                    return FoodType.cooked_fish_onion;
                } else if (containedItem == FoodType.chopped_tomato) {
                    return FoodType.cooked_fish_tomato;
                }
                break;
            case chopped_tomato:
                if (containedItem == FoodType.cooked_meat) {
                    return FoodType.cooked_meat_tomato;
                } else if (containedItem == FoodType.cooked_fish) {
                    return FoodType.cooked_fish_tomato;
                }
                break;
            case chopped_onion:
                if (containedItem == FoodType.cooked_meat) {
                    return FoodType.cooked_meat_onion;
                } else if (containedItem == FoodType.cooked_fish) {
                    return FoodType.cooked_fish_onion;
                }
                break;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Placemat(" + containedItem + ")";
    }

    @Override
    public void dispose() {
        for (Texture texture : foodTextures.values()) {
            texture.dispose();
        }
    }
}
