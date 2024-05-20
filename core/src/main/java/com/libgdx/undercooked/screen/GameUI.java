package com.libgdx.undercooked.screen;

import com.libgdx.undercooked.UIUpdater;
import com.libgdx.undercooked.entities.PlayerManager.Player;
import com.libgdx.undercooked.entities.Orders;
import com.libgdx.undercooked.entities.Orders.FoodOrder;
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

import java.util.ArrayList;

import static com.libgdx.undercooked.GameManager.score;
import static com.libgdx.undercooked.GameManager.timesUp;

public class GameUI  implements UIUpdater {
    private final Main context;
    private final Stage stage;
    private ImageButton pauseButton;
    private TextureRegion[] buttonRegions;
    private int currentIndex = 0;
    private boolean isHovered = false;
    private float elapsedTime = 18f;
    private Label timerLabel;
    private Label scoreLabel;
    private Table orderTable;
    private Orders orders;

    public GameUI(final Main context){
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

        Table rootTable = new Table();
        rootTable.top().right();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        scoreLabel = new Label("Score: " + score, skin);
        rootTable.add(scoreLabel).pad(10).expandX().align(Align.right);

        timerLabel = new Label("", skin);
        timerLabel.setPosition(10, Gdx.graphics.getHeight() - 30);
        stage.addActor(timerLabel);

        // Initialize orders table
        orderTable = new Table();
        orderTable.top().right();
        rootTable.row();
        rootTable.add(orderTable).pad(10).align(Align.right);

        stage.addActor(pauseButton);

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
        scoreLabel.setText("Score: " + score);
        elapsedTime -= Gdx.graphics.getDeltaTime();
        int minutes = (int) (elapsedTime / 60);
        int seconds = (int) (elapsedTime % 60);
        timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
        if(elapsedTime <= 0){
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

        for (FoodOrder order : orderList) {
            Label orderLabel = new Label(order.getFoodType().toString(), skin);
            orderTable.add(orderLabel).pad(5).row();
        }
    }
}
