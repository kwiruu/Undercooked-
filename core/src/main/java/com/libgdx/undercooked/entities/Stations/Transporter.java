package com.libgdx.undercooked.entities.Stations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

public class Transporter extends Station {
    Vector2 target;
    public Transporter(World world, float x, float y, int width, int height, SpriteBatch batch, int tx, int ty) {
        super(world, x, y, width, height, batch);
        target = new Vector2((float) tx, (float) ty);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("question_icon");
    }
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        stateTime += .2f;
        TextureRegion currentFrame = floatingIconFrames[0].get((int) (stateTime / frameDuration) % floatingIconFrames[0].size);
        batch.draw(currentFrame, getX(), getY());
    }
    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        p.transport(target);
        return true;
    }

    @Override
    public String toString() {
        return "Transporter";
    }
}
