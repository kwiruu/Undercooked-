package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.libgdx.undercooked.utils.Constants.PPM;

public abstract class Npc2 {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    SpriteBatch batch;

    public Npc2(int x, int y, int height, int width, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.height = (int) (width / 2 / PPM);
        this.width = (int) (height / 2 / PPM);
        this.batch = batch;
    }

    public void orbitPlayer() {

    }
}
