package com.libgdx.undercooked.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TiledObjectUtil {
    public static void parseTiledObjectLayer(World world, MapObjects objects){
        for(MapObject object : objects){
            Shape shape;
//            List<String> test = new ArrayList<String>();
//            if(test instanceof ArrayList){
//
//            }
            if(object instanceof PolylineMapObject) {
                shape = createPolyline((PolylineMapObject) object);
            } else{
                continue;
            }

                Body body;
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                body = world.createBody(bodyDef);
                body.createFixture(shape, 1.0f);
                shape.dispose();


        }
    }

    private static ChainShape createPolyline(PolylineMapObject polyline){
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldvertices = new Vector2[vertices.length/2];
        System.out.println("chainshape is running");
        for(int i = 0; i < worldvertices.length; i++){
            worldvertices[i] = new Vector2(vertices[i*2] / Constants.PPM,vertices[i*2+1] / Constants.PPM);
        }
        ChainShape cs = new ChainShape();
        cs.createChain(worldvertices);

        return cs;
    }
}
