package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.utils.CreateBox;

import static com.libgdx.undercooked.utils.Constants.PPM;

public abstract class Npc2 {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    SpriteBatch batch;
    Body body;

    public Npc2(World world, int x, int y, int height, int width, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.width = (int) ((float) height / 2 / PPM);
        this.height = (int) (height / 2 / PPM);
        this.batch = batch;
        body = CreateBox.createBox(world, (int) x, (int) y, width, height, true);
    }
}
