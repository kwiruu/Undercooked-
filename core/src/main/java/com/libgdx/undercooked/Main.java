package com.libgdx.undercooked;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.libgdx.undercooked.screen.ScreenType;

import java.util.EnumMap;

import static database.SQLOperations.*;

public class Main extends Game {
    private static final String TAG = Main.class.getSimpleName();
    private EnumMap<ScreenType, Screen> screenCache;

    @Override
    public void create() {

        createTable("tblAccount");
        insertAccount("valceven",1);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        screenCache = new EnumMap<ScreenType, Screen>(ScreenType.class);
        setScreen(ScreenType.LOADING);
    }

    public void setScreen(final ScreenType screenType) {
        final Screen screen = screenCache.get(screenType);

        if (screen == null) {
            try {
                Gdx.app.debug(TAG, "Creating new screen: " + screenType);
                final Screen newScreen = (Screen) ClassReflection.getConstructor(screenType.getScreenClass(), Main.class).newInstance(this);
                screenCache.put(screenType, newScreen);
                setScreen(newScreen);
            } catch (ReflectionException e) {
                throw new GdxRuntimeException("Could not create screen: " + screenType, e);
            }
        } else {
            Gdx.app.debug(TAG, "Switching to screen: " + screenType);
            setScreen(screen);
        }
    }


}
