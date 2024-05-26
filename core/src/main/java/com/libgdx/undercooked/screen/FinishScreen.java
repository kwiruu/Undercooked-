package com.libgdx.undercooked.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.libgdx.undercooked.Main;

import static com.libgdx.undercooked.GameManager.*;

public class FinishScreen implements Screen {
    private final Main context;
    private final float elapsedTime;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;
    private Stage stage;
    private Skin skin;

    // Declare texture variables
    private Texture winTexture;
    private Texture loseTexture;
    private Texture backgroundTexture;

    public FinishScreen(Main context, float elapsedTime) {
        this.context = context;
        this.elapsedTime = elapsedTime;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false);
        this.skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json")); // Initialize the skin
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        // Load textures
        winTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/youwin_text.png"));
        loseTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/youlose_text.png"));
        backgroundTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/bg.png"));

        // Create a label for the score using the skin
        Label scoreLabel = new Label("Score: " + score, skin);
        scoreLabel.setPosition(Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f-50);

        // Calculate minutes and remaining seconds
        int minutes = (int) elapsedTime / 60;
        int seconds = (int) elapsedTime % 60;

        // Format the elapsed time
        String formattedTime = String.format("Time: %02d:%02d", minutes, seconds);

        // Create a label for the elapsed time using the skin
        Label timeLabel = new Label(formattedTime, skin);
        timeLabel.setPosition(Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f - 100);

        // Add labels to the stage
        stage.addActor(scoreLabel);
        stage.addActor(timeLabel);

        // Add stage to input processor (if you have buttons or interactive elements)
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Draw the background texture
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw win or lose image
        if (win) {
            batch.draw(winTexture,
                Gdx.graphics.getWidth() / 2 - (winTexture.getWidth() * 3) / 2,
                Gdx.graphics.getHeight() / 2 - (winTexture.getHeight() * 3) / 2 + 150,
                winTexture.getWidth() * 3,
                winTexture.getHeight() * 3);
        } else {
            batch.draw(loseTexture,
                Gdx.graphics.getWidth() / 2 - (loseTexture.getWidth() * 3) / 2,
                Gdx.graphics.getHeight() / 2 - (loseTexture.getHeight() * 3) / 2 + 150,
                loseTexture.getWidth() * 3,
                loseTexture.getHeight() * 3);
        }

        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        stage.dispose();
        winTexture.dispose();
        loseTexture.dispose();
        backgroundTexture.dispose();
        skin.dispose(); // Dispose of the skin
        score = 0;
    }
}
