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

import java.util.Random;

import static com.libgdx.undercooked.GameManager.*;

public class FinishScreen implements Screen {
    private final float elapsedTime;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final BitmapFont font;
    private Stage stage;
    private final Skin skin;

    private Texture winTexture;
    private Texture loseTexture;
    private Texture backgroundTexture;
    private Texture scorebox;

    private Label scoreLabel;
    private Label timeLabel;

    private float randomDisplayTime = 2.0f; // Duration to display random numbers
    private float randomTimeElapsed = 0.0f;

    private Random random;

    public FinishScreen(Main context, float elapsedTime) {
        this.elapsedTime = elapsedTime;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.setToOrtho(false);
        this.skin = new Skin(Gdx.files.internal("assets/ui/ui-skin.json")); // Initialize the skin
        this.random = new Random();
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        winTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/youwin_text.png"));
        loseTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/youlose_text.png"));
        backgroundTexture = new Texture(Gdx.files.internal("assets/screens/title_screen/bg.png"));
        scorebox = new Texture(Gdx.files.internal("assets/screens/title_screen/score-box.png"));

        scoreLabel = new Label("", skin);
        scoreLabel.setPosition(Gdx.graphics.getWidth() / 2f - 30, Gdx.graphics.getHeight() / 2f-50);

        timeLabel = new Label("", skin);
        timeLabel.setPosition(Gdx.graphics.getWidth() / 2f - 30, Gdx.graphics.getHeight() / 2f - 100);

        stage.addActor(scoreLabel);
        stage.addActor(timeLabel);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (win) {
            batch.draw(winTexture,
                (float) Gdx.graphics.getWidth() / 2 - (float) (winTexture.getWidth() * 3) / 2,
                (float) Gdx.graphics.getHeight() / 2 - (float) (winTexture.getHeight() * 3) / 2 + 150,
                winTexture.getWidth() * 3,
                winTexture.getHeight() * 3);
        } else {
            batch.draw(loseTexture,
                (float) Gdx.graphics.getWidth() / 2 - (float) (loseTexture.getWidth() * 3) / 2,
                (float) Gdx.graphics.getHeight() / 2 - (float) (loseTexture.getHeight() * 3) / 2 + 150,
                loseTexture.getWidth() * 3,
                loseTexture.getHeight() * 3);
        }

        batch.draw(scorebox,
            (float) (Gdx.graphics.getWidth()) / 2 - (scorebox.getWidth() * 1.5f) / 2,
            (float) (Gdx.graphics.getHeight()) / 2 - (scorebox.getHeight() * 1.5f) / 2 - 80,
            scorebox.getWidth() * 1.5f,
            scorebox.getHeight() * 1.5f);

        batch.end();

        randomTimeElapsed += deltaTime;
        if (randomTimeElapsed < randomDisplayTime) {
            // Display random numbers
            scoreLabel.setText("Score: " + random.nextInt(1000));
            int randomMinutes = random.nextInt(60);
            int randomSeconds = random.nextInt(60);
            String formattedRandomTime = String.format("Time: %02d:%02d", randomMinutes, randomSeconds);
            timeLabel.setText(formattedRandomTime);
        } else {
            // Display the actual score and elapsed time
            scoreLabel.setText("Score: " + score);

            int minutes = (int) elapsedTime / 60;
            int seconds = (int) elapsedTime % 60;
            String formattedTime = String.format("Time: %02d:%02d", minutes, seconds);
            timeLabel.setText(formattedTime);
        }

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
        skin.dispose();
        score = 0;
    }
}
