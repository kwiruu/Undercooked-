package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Screen;
import com.libgdx.undercooked.Main;

/**
 * Every screen in the game. Each one knows how to build itself, which avoids reflection
 * (reflection needs extra setup in the browser build).
 */
public enum ScreenType {

    GAME {
        @Override public Screen create(Main context) { return new GameScreen(context); }
    },
    PAUSE {
        @Override public Screen create(Main context) { return new PauseScreen(context); }
    },
    LANDING {
        @Override public Screen create(Main context) { return new LandingPageScreen(context); }
    },
    OPTIONS {
        @Override public Screen create(Main context) { return new OptionsScreen(context); }
    },
    SELECTMAP {
        @Override public Screen create(Main context) { return new SelectionScreen(context); }
    },
    MAINMENUTRANSITION {
        @Override public Screen create(Main context) { return new MainMenuTransition(context); }
    };

    public abstract Screen create(Main context);
}
