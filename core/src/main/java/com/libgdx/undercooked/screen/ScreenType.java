package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Screen;

public enum ScreenType {

    GAME(GameScreen.class),
    LOADING(LoadingScreen.class),
    LANDING(LandingPageScreen.class);

    private final Class <? extends Screen> screenClass;
    ScreenType(final Class<? extends Screen> screenClass) {
        this.screenClass = screenClass;
    }
    public Class<? extends Screen> getScreenClass() {
        return screenClass;
    }
}
