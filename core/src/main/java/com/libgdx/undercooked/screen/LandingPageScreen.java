package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;
import com.libgdx.undercooked.AudioManager.MainMenuSound;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import database.GameData;
import database.HighScore;
import database.Leaderboard;

import java.util.List;

import static database.GameData.userSignIn;

public class LandingPageScreen implements Screen {
    private final Main context;
    private TweenManager tweenManager;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    private Table mapTable;
    static String username = null;
    private Texture imgTexture;
    private MainMenuSound mainMenuSound;
    private Sprite blockClouds1, blockClouds2;

    private Table userTable;
    private SelectBox<String> userSelectBox;
    private TextField usernameField;
    private Texture backgroundTexture;
    private Texture landingBox;

    public LandingPageScreen(final Main context) {
        this.context = context;
        this.mainMenuSound = new MainMenuSound();
    }

    @Override
    public void show() {
        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage);

        imgTexture = new Texture(Gdx.files.internal("sprites_raw/login_box2.png"));

        Texture blockCloud1Texture = new Texture(Gdx.files.internal("screens/title_screen/block_clouds1.png"));
        Texture blockCloud2Texture = new Texture(Gdx.files.internal("screens/title_screen/block_clouds2.png"));

        blockClouds1 = new Sprite(blockCloud1Texture);
        blockClouds2 = new Sprite(blockCloud2Texture);

        float targetX = Gdx.graphics.getWidth() / 2f - blockClouds1.getWidth() / 2;

        blockClouds1.setSize(blockCloud1Texture.getWidth() * 4, blockCloud1Texture.getHeight() * 4);
        blockClouds2.setSize(blockCloud2Texture.getWidth() * 4, blockCloud2Texture.getHeight() * 4);

        blockClouds1.setPosition(Gdx.graphics.getWidth() / 2f - blockClouds1.getWidth() / 2, Gdx.graphics.getHeight() / 2f - blockClouds1.getHeight() / 2);
        blockClouds2.setPosition(Gdx.graphics.getWidth() / 2f - blockClouds2.getWidth() / 2, Gdx.graphics.getHeight() / 2f - blockClouds2.getHeight() / 2);

        Tween.to(blockClouds1, SpriteAccessor.POS_X, 3f)
            .target(-blockClouds1.getWidth())
            .start(tweenManager);
        Tween.to(blockClouds2, SpriteAccessor.POS_X, 3f)
            .target(Gdx.graphics.getWidth())
            .start(tweenManager);

        skin = new Skin(Gdx.files.internal("metal-ui.json"));
        backgroundTexture = new Texture(Gdx.files.internal("screens/title_screen/bg.png"));
        landingBox = new Texture(Gdx.files.internal("screens/title_screen/landing_box.png"));

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // User table setup
        userTable = new Table();
        float padding = 20f;

        userTable.add(new Label("Select User:", skin)).pad(padding).left();
        userSelectBox = new SelectBox<>(skin);
        loadUserSelectBoxData();
        userSelectBox.setSelected(null);

        userTable.add(userSelectBox).width(200).pad(padding).left();

        userTable.row().padTop(padding);

        userTable.add(new Label("Or Enter Username:", skin)).pad(padding).left();
        usernameField = new TextField("", skin);
        userTable.add(usernameField).width(200).pad(padding).left();

        userTable.row().padTop(padding);

        TextButton okButton = new TextButton("Sign In", skin);
        userTable.add(okButton).width(100).height(45).pad(padding).left();

        userSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String selectedUser = userSelectBox.getSelected();
                if (selectedUser != null && !selectedUser.equals("Select a user")) {
                    usernameField.setText(selectedUser);
                } else {
                    usernameField.setText("");
                }
            }
        });

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String selectedUser = userSelectBox.getSelected();
                username = usernameField.getText().trim();

                if ((selectedUser == null || selectedUser.equals("Select a user")) && username.isEmpty()) {
                    Dialog warningDialog = new Dialog("Warning", skin);
                    warningDialog.text("Please select or enter a username.");
                    warningDialog.button("OK", true);
                    warningDialog.show(stage);
                } else {
                    if (!username.isEmpty()) {
                        selectedUser = username;
                    } else {
                        usernameField.setText(selectedUser);
                    }

                    if (userSignIn(selectedUser)) {
                        // Use the cleaned-up name that was actually saved.
                        username = GameData.cleanName(selectedUser);
                        context.setScreen(ScreenType.SELECTMAP);
                    } else {
                        Dialog warningDialog = new Dialog("Warning", skin);
                        warningDialog.text("Sign-in failed. Please try again.");
                        warningDialog.button("OK", true);
                        warningDialog.show(stage);
                    }
                }
            }
        });

        mapTable = new Table();
        setupMapButtons(skin);
        Table parentTable = new Table();
        parentTable.add(userTable).expandY().center().pad(20).left();
        parentTable.add(mapTable).expandY().center().pad(20).right();

        root.add(parentTable).expand().center();

        root.background(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tweenManager.update(delta);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(landingBox, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        blockClouds1.draw(batch);
        blockClouds2.draw(batch);
        batch.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            Main.deleteScreen(ScreenType.LANDING);
            context.setScreen(ScreenType.MAINMENUTRANSITION);
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void setupMapButtons(Skin skin) {
        mapTable.clear();
        mapTable.add(new Label("WORLD HALL OF FAME:", skin)).pad(20f).left().row();

        for (int i = 1; i <= 5; i++) {
            final int mapNumber = i;

            TextButton highScoreButton = new TextButton(String.valueOf(mapNumber), skin);
            highScoreButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showHighScoresDialog(mapNumber);
                }
            });

            mapTable.add(highScoreButton).width(200F).pad(10);
            mapTable.row();
        }
    }

    private void showHighScoresDialog(final int mapId) {
        final Dialog dialog = new Dialog("Map " + mapId + " - Top 10", skin);
        final Table content = dialog.getContentTable();
        content.add(new Label("Loading...", skin)).pad(10);
        dialog.button("Close", true);
        dialog.show(stage);

        // The leaderboard is online, so the scores arrive a moment later and fill in the open dialog.
        Leaderboard.fetchTopScores(mapId, new Leaderboard.Callback() {
            @Override
            public void onResult(List<HighScore> highScores) {
                content.clear();
                if (highScores.isEmpty()) {
                    content.add(new Label("No scores yet. Be the first!", skin)).pad(10);
                } else {
                    content.add(new Label("#", skin)).pad(10);
                    content.add(new Label("Player", skin)).pad(10);
                    content.add(new Label("Score", skin)).pad(10);
                    content.row();
                    int rank = 1;
                    for (HighScore highScore : highScores) {
                        content.add(new Label(String.valueOf(rank++), skin)).pad(6);
                        content.add(new Label(highScore.getUserName(), skin)).pad(6).left();
                        content.add(new Label(String.valueOf(highScore.getHighScore()), skin)).pad(6);
                        content.row();
                    }
                }
                dialog.pack();
                dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2f),
                    Math.round((stage.getHeight() - dialog.getHeight()) / 2f));
            }

            @Override
            public void onError(String message) {
                content.clear();
                content.add(new Label(message, skin)).pad(10);
                dialog.pack();
                dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2f),
                    Math.round((stage.getHeight() - dialog.getHeight()) / 2f));
            }
        });
    }

    private void loadUserSelectBoxData() {
        List<String> users = GameData.getAccounts();
        users.add(0, "Select a user");
        userSelectBox.setItems(users.toArray(new String[0]));
        userSelectBox.setSelected("Select a user");
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    public static String getUsername() {
        return username;
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        mainMenuSound.dispose();
    }
}
