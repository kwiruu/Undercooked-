package com.libgdx.undercooked;

import com.badlogic.gdx.Game;
import screen.LoadingScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new LoadingScreen());
    }
}
