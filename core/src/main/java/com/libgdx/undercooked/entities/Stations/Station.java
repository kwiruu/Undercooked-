package com.libgdx.undercooked.entities.Stations;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.libgdx.undercooked.entities.FoodType;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.utils.CreateBox;

import static com.libgdx.undercooked.utils.Constants.PPM;

public abstract class Station {
    //temporarily stopped extends Entity
    FoodType containedItem;
    private Body body;
    private final float x;
    private final float y;
    private final int width;
    private final int height;
    protected float frameDuration;
    protected float stateTime; // Time elapsed since the start of the animation
    SpriteBatch batch;
    protected static TextureAtlas floating_iconAtlas = new TextureAtlas("assets/floating_icons/float_icons.atlas");
    protected Array<TextureAtlas.AtlasRegion>[] floatingIconFrames = new Array[3];
    @SuppressWarnings("unchecked")
    public Station(World world, float x, float y, int width, int height, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.width = (int) (width / 2 / PPM);
        this.height = (int) (height / 2 / PPM);
        this.batch = batch;
        body = CreateBox.createBox(world, (int) x, (int) y, width, height, true);
        frameDuration = 1f;
        stateTime = 0f;
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
    public abstract boolean interact(Player p);

    @Override
    public abstract String toString();

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
