package com.libgdx.undercooked.entities.Npc2;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.entities.Stations.Station;
import com.libgdx.undercooked.utils.CreateBox;

import java.awt.*;
import java.util.ArrayList;

public class ProjectileManager {
    // implements ContactListener removed
    public ArrayList<Projectile> projList = new ArrayList<>();
    public Player player;
    public ProjectileManager(Player p) {
        player = p;
    }
    public void spawnProjectile(World world, int x, int y, int w, int h, SpriteBatch batch) {
        for (Projectile p : projList) {
            if (!p.active) {
                p.reuseProjectile(world, x, y, w, h, batch);
                return;
            }
        }
        projList.add(new Projectile(world, x, y, w, h, batch));
    }
    public void render() {
        for (Projectile p : projList) {
            if (p.active) p.render();
        }
    }
    public void update() {
        for (Projectile p : projList) {
            if (p.active) {
                p.update();
                checkPlayerCollision(p);
            }
        }
    }
    void checkPlayerCollision(Projectile pr) {
        com.badlogic.gdx.math.Rectangle r;
        r = new Rectangle(pr.getX(), pr.getY(), pr.getWidth(), pr.getHeight());
        if (r.contains(player.getPosition())) {
            collision(pr);
        }
    }
    void collision(Projectile pr) {
        player.hitByProjectile();
        pr.deactivate();
    }

//    @Override
//    public void beginContact(Contact contact) {
//        Fixture fixtureA = contact.getFixtureA();
//        Fixture fixtureB = contact.getFixtureB();
//
//        System.out.println("Collision detected between " + fixtureA.getBody().getUserData() + " and " + fixtureB.getBody().getUserData());
//
//    }
//
//    @Override
//    public void endContact(Contact contact) {
//        // negative edge collision, leave empty
//    }
//
//    @Override
//    public void preSolve(Contact contact, Manifold oldManifold) {
//        // property change on contact, leave empty
//    }
//
//    @Override
//    public void postSolve(Contact contact, ContactImpulse impulse) {
//        // get collision response data, leave empty
//    }
}

class Projectile {
    private int x;
    private int y;
    private int width;
    private int height;
    SpriteBatch batch;
    Body body;
    ShapeRenderer rect = new ShapeRenderer();
    boolean active = true;
    public Projectile(World world, int x, int y, int width, int height, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.batch = batch;
        body = CreateBox.createBox(world, (int) x, (int) y, width, height, false);
    }
    public void reuseProjectile(World world, int x, int y, int width, int height, SpriteBatch batch) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.batch = batch;
        body = CreateBox.createBox(world, (int) x, (int) y, width, height, false);
        body.setUserData("Projectile");
    }
    public void render() {
        rect.begin(ShapeRenderer.ShapeType.Filled);
        rect.setColor(1, 0, 0, 1);
        rect.rect(x-1, y-1, width+2, height+2);
        rect.setColor(1, 1, 1, 1);
        rect.rect(x, y, width, height);
        rect.end();
    }

    public void update() {
    }

    public void deactivate() {
        active = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
