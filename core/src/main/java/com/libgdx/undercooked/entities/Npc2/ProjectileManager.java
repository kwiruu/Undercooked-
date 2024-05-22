package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.utils.CreateBox;

import java.util.ArrayList;

public class ProjectileManager {
    public ArrayList<Projectile> projList = new ArrayList<>();
    public ProjectileManager() {

    }

}

class Projectile {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    SpriteBatch batch;
    Body body;
    public Projectile(World world, int x, int y, int width, int height, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.batch = batch;
        body = CreateBox.createBox(world, (int) x, (int) y, width, height, false);
    }
}
