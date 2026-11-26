package com.libgdx.undercooked.teavm;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.libgdx.undercooked.Main;

/** Starts the game in the browser. */
public class TeaVMLauncher {
    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        // -1 = use the size of the <canvas> element on the page (set in TeaVMBuilder).
        config.width = -1;
        config.height = -1;
        // Keeps this game's saved profiles separate from other libGDX games on the same site.
        config.storagePrefix = "undercooked";
        new TeaApplication(new Main(), config);
    }
}
