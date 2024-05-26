package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import static com.libgdx.undercooked.utils.Constants.PPM;

public abstract class Npc2 {
    private float x;
    private float y;
    private final int width;
    private final int height;
    SpriteBatch batch;
    protected static TextureAtlas iconAtlas = new TextureAtlas("assets/floating_icons/float_icons.atlas");
    protected Array<TextureAtlas.AtlasRegion> iconFrames = new Array<>();
    boolean isActive = true;
    int spriteCtr = 0;

    public Npc2(float x, float y, int height, int width, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.height = (int) (width / 2 / PPM);
        this.width = (int) (height / 2 / PPM);
        this.batch = batch;
    }

    public abstract void render();
    public abstract void update();

    @Override
    public String toString() {
        return "NPC";
    }

    public void orbitPlayer() {

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
