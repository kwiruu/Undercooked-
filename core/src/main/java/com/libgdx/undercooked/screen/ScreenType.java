package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Screen;

public enum ScreenType {

    GAME(GameScreen.class),
    PAUSE(PauseScreen.class),
    LANDING(LandingPageScreen.class),
    OPTIONS(OptionsScreen.class),
    SELECTMAP(SelectionScreen.class),
    MAINMENUTRANSITION(MainMenuTransition.class);

    private final Class <? extends Screen> screenClass;
    ScreenType(final Class<? extends Screen> screenClass) {
        this.screenClass = screenClass;
    }
    public Class<? extends Screen> getScreenClass() {
        return screenClass;
    }
}
