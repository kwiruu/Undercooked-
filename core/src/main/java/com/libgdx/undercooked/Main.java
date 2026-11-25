package com.libgdx.undercooked;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.libgdx.undercooked.screen.*;

import java.util.EnumMap;


public class Main extends Game implements SplashScreen.SplashScreenListener{
    private static final String TAG = "Main";
    private static EnumMap<ScreenType, Screen> screenCache;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        screenCache = new EnumMap<ScreenType, Screen>(ScreenType.class);

        SplashScreen splashScreen = new SplashScreen(this);
        splashScreen.setListener(new SplashScreen.SplashScreenListener() {
            @Override
            public void onSplashScreenFinished() {
                // NOTE debug

                setScreen(ScreenType.MAINMENUTRANSITION);
                //setScreen(ScreenType.GAME);
            }
        });
        setScreen(splashScreen);
    }

    public void switchToGameScreen() {
        setScreen(new GameScreen(this));
    }

    public void switchToFinishScreen(int score, float timeFinished) {
        setScreen(new FinishScreen(this, timeFinished));
    }

    public void switchToMainMenuScreen() {
        setScreen(new SelectionScreen(this));
    }

    @Override
    public void onSplashScreenFinished() {
        // Transition to GameScreen after splash animation completes
        setScreen(ScreenType.LANDING);
    }

    public void setScreen(final ScreenType screenType) {
        final Screen screen = screenCache.get(screenType);

        if (screen == null) {
            Gdx.app.debug(TAG, "Creating new screen: " + screenType);
            final Screen newScreen = screenType.create(this);
            screenCache.put(screenType, newScreen);
            setScreen(newScreen);
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
