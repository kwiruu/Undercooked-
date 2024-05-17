package com.libgdx.undercooked.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.utils.CreateBox;

public abstract class Station {
    //temporarily stopped extends Entity
    FoodType containedItem;
    private Body body;
    private final float x;
    private final float y;
    private final int width;
    private final int height;
    protected TextureAtlas floating_iconAtlas;
    protected float frameDuration;
    protected float stateTime; // Time elapsed since the start of the animation
    SpriteBatch batch;
    protected Array<TextureAtlas.AtlasRegion> floatingIconFrames;

    // TODO popup (progress bar for non-source stations)

    public Station(World world, float x, float y, int width, int height, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.batch = batch;
        body = CreateBox.createBox(world, (int) x, (int) y, width, height, true);
        Array<TextureAtlas.AtlasRegion> floatingIconFrames;
        floating_iconAtlas = new TextureAtlas("assets/floating_icons/float_icons.atlas");
        frameDuration = 1f; // Set the duration of each frame (adjust as needed)
        stateTime = 0f; // Initialize stateTime
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }
    public void setBody(Body body) {
        this.body = body;
    }

    public abstract void render();
    public abstract void interact(Player p);

    @Override
    public abstract String toString();
}
