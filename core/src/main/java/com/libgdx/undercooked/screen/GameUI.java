package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameUI {
    private Stage stage;
    private ImageButton pause_button;
    private TextureAtlas atlas;
    private TextureRegion[] buttonRegions;
    private int currentIndex = 0;

    public GameUI(Stage stage) {
        this.stage = stage;
        this.atlas = new TextureAtlas(Gdx.files.internal("assets/ui/buttonsAtlas.atlas"));
        // Assuming you have loaded the regions for "pause_button" in your atlas
        buttonRegions = new TextureRegion[2];
        buttonRegions[0] = atlas.findRegion("pause_button", 0); // Unclicked region
        buttonRegions[1] = atlas.findRegion("pause_button", 1); // Clicked region
    }

    public void createButtons() {
        // Create the ImageButton
        pause_button = new ImageButton(
            new TextureRegionDrawable(buttonRegions[0]),
            new TextureRegionDrawable(buttonRegions[1])
        );

        // Set the button size (64x64)
        pause_button.setSize(64, 64);
        pause_button.setPosition((float) (Gdx.graphics.getWidth()) / 2-32, Gdx.graphics.getHeight() - 72);

        // Add a click listener to handle button clicks
        pause_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Toggle between unclicked and clicked regions
                currentIndex = (currentIndex + 1) % 2;
                pause_button.getStyle().imageUp = new TextureRegionDrawable(buttonRegions[currentIndex]);
            }
        });

        // Add the button to the stage
        stage.addActor(pause_button);
    }
}
