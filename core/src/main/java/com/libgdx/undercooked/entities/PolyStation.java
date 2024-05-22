package com.libgdx.undercooked.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.libgdx.undercooked.entities.PlayerManager.Player;

import java.util.ArrayList;

public class PolyStation extends Station implements canUpdate {
    int max_timer = 5;
    float timer = max_timer;
    boolean activeStation1 = true;
    ArrayList<Station> stations = new ArrayList<>();
    public PolyStation(World world, float x, float y, int width, int height, SpriteBatch batch, Station s1, Station s2) {
        super(world, x, y, width, height, batch);
        stations.add(s1);
        stations.add(s2);
        floatingIconFrames[0] = floating_iconAtlas.findRegions("question_icon");
    }
    public void render() {
        if (activeStation1) stations.get(0).render();
        else stations.get(1).render();
    }
    @Override
    public void update(float deltaTime) {
        int as = activeStation1 ? 0 : 1;
        if (stations.get(as) instanceof canUpdate) ((canUpdate) stations.get(as)).update(deltaTime);

        if (timer > 0) {
            timer -= deltaTime;
        } else {
            timer = max_timer;
            activeStation1 = !activeStation1;
        }
    }
    @Override
    public boolean interact(Player p) {
        System.out.println("interacted with a " + this);
        if (activeStation1) return stations.get(0).interact(p);
        else return stations.get(1).interact(p);
    }

    @Override
    public String toString() {
        return "PolyStation";
    }
}
