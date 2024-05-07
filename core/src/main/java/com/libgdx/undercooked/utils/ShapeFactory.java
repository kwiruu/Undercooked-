package com.libgdx.undercooked.utils;

import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class ShapeFactory {

    public static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < worldVertices.length; i++) {
            worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
        }

        ChainShape cs = new ChainShape();
        cs.createChain(worldVertices);

        return cs;
    }

    public static Shape createRectangle(RectangleMapObject object) {
        float x = object.getRectangle().x;
        float y = object.getRectangle().y;
        float width = object.getRectangle().width;
        float height = object.getRectangle().height;

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM, new Vector2((x + width / 2f) / Constants.PPM, (y + height / 2f) / Constants.PPM), 0);
        return ps;
    }
}
