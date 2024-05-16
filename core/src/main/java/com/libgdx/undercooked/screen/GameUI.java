package com.libgdx.undercooked.screen;

import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

public class GameUI{
    private final Main context;
    private final Stage stage;
    private ImageButton pause_button;
    private TextureRegion[] buttonRegions;
    private int currentIndex = 0;
    private boolean isHovered = false;

    private float elapsedTime = 10f;
    private Label timerLabel;

    private Label usernameLabel;

    Vector2 playerPos = new Vector2();


    public GameUI(final Main context) {
        this.context = context;

        this.stage = new Stage(new ScreenViewport());
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("assets/ui/buttonsAtlas.atlas"));
        buttonRegions = new TextureRegion[2];
        buttonRegions[0] = atlas.findRegion("pause_button", 0); // Unclicked region
        buttonRegions[1] = atlas.findRegion("pause_button", 1); // Clicked region
        initializeComponents();
    }

    public void initializeComponents() {
        pause_button = new ImageButton(
            new TextureRegionDrawable(buttonRegions[0]),
            new TextureRegionDrawable(buttonRegions[1])
        );

        pause_button.setSize(64, 64);
        pause_button.setPosition((float) (Gdx.graphics.getWidth()) / 2 - 32, Gdx.graphics.getHeight() - 72);



        pause_button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Toggle between unclicked and clicked regions
                currentIndex = (currentIndex + 1) % 2;
                pause_button.getStyle().imageUp = new TextureRegionDrawable(buttonRegions[currentIndex]);
                context.setScreen(ScreenType.LOADING);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                isHovered = true;
                updateButtonPosition();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                isHovered = false;
                updateButtonPosition();
            }
        });


        Skin skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        Table rootTable = new Table();
        rootTable.top().right();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        usernameLabel = new Label("Position: " + playerPos, skin);
        rootTable.add(usernameLabel).pad(10).expandX().align(Align.right);

        timerLabel = new Label("", skin);
        timerLabel.setPosition(10, Gdx.graphics.getHeight() - 30);
        stage.addActor(timerLabel);

        stage.addActor(pause_button);
    }

    public void updateButtonPosition() {
        float displacement = isHovered ? 2.5f : 0f;
        pause_button.setPosition((float) (Gdx.graphics.getWidth()) / 2 - 32, Gdx.graphics.getHeight() - 72 + displacement);
    }

    public void render() {
        Gdx.input.setInputProcessor(stage);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.LOADING);
        }
    }

    public void update(Player player){
        playerPos.set((player.getPosition().x), player.getPosition().y);
        usernameLabel.setText("Position: " + playerPos);


        elapsedTime -= Gdx.graphics.getDeltaTime();
        int minutes = (int) (elapsedTime / 60);
        int seconds = (int) (elapsedTime % 60);
        timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    }

    public Stage getStage() {
        return stage;
    }
}
