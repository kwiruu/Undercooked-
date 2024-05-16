package com.libgdx.undercooked.entities.Npc;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.libgdx.undercooked.entities.Npc.components.NpcB2D;
import com.libgdx.undercooked.entities.Npc.components.NpcComponent;

import static com.libgdx.undercooked.utils.Constants.PPM;

public class Npc extends PooledEngine {

    private World world;
    private NpcB2D npcB2D;

    public Npc(World world) {
        super();
        this.world = world;
    }

    public void createNpc(final Vector2 npcSpawnLocation) {

        final Entity entity = this.createEntity();

        entity.add(this.createComponent(NpcComponent.class));

        npcB2D = this.createComponent(NpcB2D.class);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(npcSpawnLocation.x / PPM, npcSpawnLocation.y / PPM);
        Body npcBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / PPM, 16 / PPM); // Assuming NPC size is 32x32
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        npcBody.createFixture(fixtureDef);
        shape.dispose();

        npcB2D.setNpcBody(npcBody);
        npcB2D.setWidth(32);
        npcB2D.setHeight(32);

        entity.add(npcB2D);

        this.addEntity(entity);
    }

    public NpcB2D getNpcB2D() {
        return npcB2D;
    }
}
