package com.libgdx.undercooked.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.libgdx.undercooked.Main;

import static com.libgdx.undercooked.utils.Constants.PPM;

public abstract class Entity {
    Body eBody;

    // constructor derived from createBox
    public Entity(float x, float y, int width, int height, boolean isStatic) {
        BodyDef def = new BodyDef();
        if (isStatic) {
            def.type = BodyDef.BodyType.StaticBody;
        } else {
            def.type = BodyDef.BodyType.DynamicBody;
        }
        def.position.set(x,y);
        def.fixedRotation = true;
        //eBody = world.createBody(def);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2 / PPM,height/2/ PPM);
        eBody.createFixture(shape,1.0f);
        shape.dispose();
    }
    // TODO either extract station position from tilemap or do manually for each map
}
