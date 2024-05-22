package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

public class Ghost extends Npc2 {
    public Ghost(World world, int x, int y, int height, int width, SpriteBatch batch) {
        super(x, y, height, width, batch);
    }
}
