package com.libgdx.undercooked.screen;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteAccessor implements TweenAccessor<Sprite> {
    public static final int ALPHA = 1;
    public static final int POS_X = 2;
    public static final int POS_Y = 3;
    public static final int ROTATION = 4;

    @Override
    public int getValues(Sprite target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ALPHA:
                returnValues[0] = target.getColor().a;
                return 1;
            case POS_X:
                returnValues[0] = target.getX();
                return 1;
            case POS_Y:
                returnValues[0] = target.getY();
                return 1;
            case ROTATION:
                returnValues[0] = target.getRotation();
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public void setValues(Sprite target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ALPHA:
                target.setAlpha(newValues[0]);
                break;
            case POS_X:
                target.setPosition(newValues[0], target.getY());
                break;
            case POS_Y:
                target.setPosition(target.getX(), newValues[0]);
                break;
            case ROTATION:
                target.setRotation(newValues[0]);
                break;
            default:
                break;
        }
    }
}
