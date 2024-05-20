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
    private Orders orders;
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

    public void initializeComponents() {
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

        //info table para babaw sa oderlist lmao
        infoTable = new Table();
        infoTable.setHeight(32);
        Texture topbackgroundTexture = new Texture(Gdx.files.internal("assets/ui/top_table_background.png"));
        TextureRegionDrawable topbackgroundDrawable = new TextureRegionDrawable(new TextureRegion(topbackgroundTexture));
        infoTable.setBackground(topbackgroundDrawable);

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
        Texture belowbackgroundTexture = new Texture(Gdx.files.internal("assets/ui/below_table_background.png"));
        TextureRegionDrawable belowbackgroundDrawable = new TextureRegionDrawable(new TextureRegion(belowbackgroundTexture));
        belowTable.setBackground(belowbackgroundDrawable);
        rootTable.add(belowTable).pad(0).expand().align(Align.topRight);;

        scoreLabel = new Label("Score: " + score, skin);
        orderTable.add(scoreLabel).pad(10).align(Align.right).row();

        timerLabel = new Label("", skin);
        orderTable.add(timerLabel).pad(10).align(Align.right).row();

        stage.addActor(pauseButton);


        infoTable.add(scoreLabel).pad(10);
        infoTable.add(timerLabel).pad(10);

        orders = new Orders(); // Initialize orders
    }

    public void updateButtonPosition() {
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
        scoreLabel.setText("  "+score+"   ");
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

        // Re-add the scoreLabel and timerLabel at the top of the orderTable

        orders.removeInactiveOrders();
        ArrayList<FoodOrder> orderList = orders.getOrderList();

        Skin skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json"));

        // Load the background texture and create a drawable
        Texture rowBackgroundTexture = new Texture(Gdx.files.internal("assets/ui/row_background.png"));
        TextureRegionDrawable rowBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(rowBackgroundTexture));

        Texture itemBackgroundTexture = new Texture(Gdx.files.internal("assets/ui/item_background.png"));
        TextureRegionDrawable itemBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(itemBackgroundTexture));

        for (FoodOrder order : orderList) {
            // Create a Table for the row
            Table rowTable = new Table();
            rowTable.padRight(10f);
            rowTable.align(Align.left);

            // Create a Table for the image with its own background
            Table imageTable = new Table();
            imageTable.setBackground(itemBackgroundDrawable);
            imageTable.align(Align.center).pad(0);

            Texture texture = new Texture(Gdx.files.internal("assets/food_sprites/foods/" + order.getFoodType().toString() + ".png"));
            Image orderImage = new Image(texture);
            imageTable.add(orderImage).pad(5).width(32f).height(32f);

            // Create a Table for the label with its own background
            Table labelTable = new Table();
            labelTable.setBackground(rowBackgroundDrawable);
            labelTable.align(Align.left);
            Label orderLabel = new Label(toCamelCase(order.getFoodType().toString()), skin);
            labelTable.add(orderLabel).pad(5).width(128f);

            // Add the image and label tables to the row table
            rowTable.add(imageTable).pad(5).width(34f).height(34f);
            rowTable.add(labelTable).pad(5).expandX().fillX();

            // Add the row table to the main orderTable
            orderTable.add(rowTable).pad(5).row();
        }
    }

    public String toCamelCase(String input) {
        // Replace underscores with spaces
        input = input.replace('_', ' ');

        // Split the string into words
        String[] words = input.split(" ");

        // Convert the first letter of each word to uppercase and the rest to lowercase
        StringBuilder camelCaseString = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                camelCaseString.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }

        // Trim the trailing space and return the result
        return camelCaseString.toString().trim();
    }
}
