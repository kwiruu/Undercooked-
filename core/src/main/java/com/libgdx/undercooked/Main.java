package com.libgdx.undercooked;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.libgdx.undercooked.screen.LandingPageScreen;
import com.libgdx.undercooked.screen.ScreenType;
import com.libgdx.undercooked.screen.SplashScreen;

import java.util.EnumMap;

import static database.SQLOperations.*;

public class Main extends Game implements SplashScreen.SplashScreenListener{
    private static final String TAG = Main.class.getSimpleName();
    private static EnumMap<ScreenType, Screen> screenCache;

    @Override
    public void create() {


//        createDatabase();
//        createTableAccount("tblAccount");
//        createTableMap();
//        createTableHighScore();

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        screenCache = new EnumMap<ScreenType, Screen>(ScreenType.class);

        SplashScreen splashScreen = new SplashScreen(this);
        splashScreen.setListener(new SplashScreen.SplashScreenListener() {
            @Override
            public void onSplashScreenFinished() {
                // NOTE debug
                //setScreen(ScreenType.MAINMENUTRANSITION);
                setScreen(ScreenType.SELECTMAP);
            }
        });
        setScreen(splashScreen);
    }

    @Override
    public void onSplashScreenFinished() {
        // Transition to GameScreen after splash animation completes
        setScreen(ScreenType.LANDING);
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

    public static void deleteScreen(ScreenType screenType) {
        Screen screen = screenCache.remove(screenType);
        if (screen != null) {
            Gdx.app.debug(TAG, "Disposing screen: " + screenType);
            screen.dispose();
        } else {
            Gdx.app.debug(TAG, "Screen not found in cache: " + screenType);
        }
    }
    public String getUsername() {
        return LandingPageScreen.getUsername();
    }

}
