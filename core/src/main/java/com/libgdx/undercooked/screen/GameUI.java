package com.libgdx.undercooked.screen;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.libgdx.undercooked.UIUpdater;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.entities.Orders;
import com.libgdx.undercooked.entities.Orders.FoodOrder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

import java.util.ArrayList;

import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.GameManager.timesUp;

public class GameUI implements UIUpdater {
    private final Main context;
    private final Stage stage;
    private ImageButton pauseButton;
    private TextureRegion[] buttonRegions;
    private int currentIndex = 0;
    private boolean isHovered = false;
    private float elapsedTime = 180f;
    private Label timerLabel;
    private Label scoreLabel;
    private Table orderTable;
    private Table rootTable;
    private Table infoTable;
    private Table belowTable;

    public GameUI(final Main context) {
        this.context = context;
        this.stage = new Stage(new ScreenViewport());
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("assets/ui/buttonsAtlas.atlas"));
        buttonRegions = new TextureRegion[2];
        buttonRegions[0] = atlas.findRegion("pause_button", 0); // Unclicked region
        buttonRegions[1] = atlas.findRegion("pause_button", 1); // Clicked region
        initializeComponents();
    }

    private void initializeComponents() {
        pauseButton = new ImageButton(
            new TextureRegionDrawable(buttonRegions[0]),
            new TextureRegionDrawable(buttonRegions[1])
        );
        pauseButton.setSize(64, 64);
        pauseButton.setPosition((float) (Gdx.graphics.getWidth()) / 2 - 32, Gdx.graphics.getHeight() - 72);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentIndex = (currentIndex + 1) % 2;
                pauseButton.getStyle().imageUp = new TextureRegionDrawable(buttonRegions[currentIndex]);
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

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.padRight(15f);
        stage.addActor(rootTable);

        infoTable = new Table();
        infoTable.setHeight(32);
        Texture topBackgroundTexture = new Texture(Gdx.files.internal("assets/ui/top_table_background.png"));
        TextureRegionDrawable topBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(topBackgroundTexture));
        infoTable.setBackground(topBackgroundDrawable);

        rootTable.add(infoTable).pad(0).align(Align.topRight);

        orderTable = new Table();
        Texture backgroundTexture = new Texture(Gdx.files.internal("assets/ui/table_background.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        orderTable.setBackground(backgroundDrawable);
        orderTable.pad(2);
        orderTable.setHeight(32);
        orderTable.top().right();
        rootTable.row();
        rootTable.add(orderTable).pad(0).align(Align.topRight);
        rootTable.row();

        belowTable = new Table();
        Texture belowBackgroundTexture = new Texture(Gdx.files.internal("assets/ui/below_table_background.png"));
        TextureRegionDrawable belowBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(belowBackgroundTexture));
        belowTable.setBackground(belowBackgroundDrawable);
        rootTable.add(belowTable).pad(0).expand().align(Align.topRight);

        scoreLabel = new Label("Score: " + score, skin);
        timerLabel = new Label("", skin);

        infoTable.add(scoreLabel).pad(10);
        infoTable.add(timerLabel).pad(10);

        stage.addActor(pauseButton);
    }

    private void updateButtonPosition() {
        float displacement = isHovered ? 2.5f : 0f;
        pauseButton.setPosition((float) (Gdx.graphics.getWidth()) / 2 - 32, Gdx.graphics.getHeight() - 72 + displacement);
    }

    public void render() {
        Gdx.input.setInputProcessor(stage);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.LOADING);
        }
    }

    public void update(Player player) {
        scoreLabel.setText("  " + score + "   ");
        elapsedTime -= Gdx.graphics.getDeltaTime();
        int minutes = (int) (elapsedTime / 60);
        int seconds = (int) (elapsedTime % 60);
        timerLabel.setText(String.format("     %02d:%02d", minutes, seconds));
        if (elapsedTime <= 0) {
            timesUp = true;
        }
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void updateOrdersUI(Orders orders) {
        orderTable.clear(); // Clear previous orders

        orders.removeInactiveOrders();
        ArrayList<FoodOrder> orderList = orders.getOrderList();

        Skin skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        Texture rowBackgroundTexture = new Texture(Gdx.files.internal("assets/ui/row_background.png"));
        TextureRegionDrawable rowBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(rowBackgroundTexture));

        Texture itemBackgroundTexture = new Texture(Gdx.files.internal("assets/ui/item_background.png"));
        TextureRegionDrawable itemBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(itemBackgroundTexture));

        for (FoodOrder order : orderList) {
            Table rowTable = new Table();
            rowTable.padRight(10f);
            rowTable.align(Align.left);

            Table imageTable = new Table();
            imageTable.setBackground(itemBackgroundDrawable);
            imageTable.align(Align.center).pad(0);

            Texture texture = new Texture(Gdx.files.internal("assets/food_sprites/foods/" + order.getFoodType().toString() + ".png"));
            Image orderImage = new Image(texture);
            imageTable.add(orderImage).pad(5).width(32f).height(32f);

            Table labelTable = new Table();
            labelTable.setBackground(rowBackgroundDrawable);
            labelTable.align(Align.left);
            Label orderLabel = new Label(toCamelCase(order.getFoodType().toString()), skin);
            labelTable.add(orderLabel).pad(5).width(128f);

            rowTable.add(imageTable).pad(5).width(34f).height(34f);
            rowTable.add(labelTable).pad(5).expandX().fillX();

            orderTable.add(rowTable).pad(5).row();
        }
    }

    private String toCamelCase(String input) {
        input = input.replace('_', ' ');
        String[] words = input.split(" ");
        StringBuilder camelCaseString = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                camelCaseString.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        return camelCaseString.toString().trim();
    }

    public float getElapsedTime() {
        return this.elapsedTime;
    }

    public void gameOverScreen() {
        createEndScreen("Game Over");
    }

    public void winScreen() {
        createEndScreen("You Win!");
    }

    private void createEndScreen(String message) {
        stage.clear(); // Clear the stage

        Skin skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        Table endScreenTable = new Table();
        endScreenTable.setFillParent(true);
        stage.addActor(endScreenTable);

        Label messageLabel = new Label(message, skin);
        messageLabel.setFontScale(2);
        endScreenTable.add(messageLabel).pad(20).row();

        Label scoreLabel = new Label("Final Score: " + score, skin);
        endScreenTable.add(scoreLabel).pad(20).row();

        int minutes = (int) (elapsedTime / 60);
        int seconds = (int) (elapsedTime % 60);
        Label timeLabel = new Label(String.format("Time: %02d:%02d", minutes, seconds), skin);
        endScreenTable.add(timeLabel).pad(20).row();

        TextButton returnButton = new TextButton("Return to Menu", skin);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.SELECTMAP); // Change this to the appropriate screen type
            }
        });
        endScreenTable.add(returnButton).pad(20).row();
    }
}
