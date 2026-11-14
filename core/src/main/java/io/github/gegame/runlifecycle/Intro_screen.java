package io.github.gegame.runlifecycle;

// import com.badlogic.gdx.Game;
// import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
<<<<<<< Updated upstream
// commit
=======

/**
 * Intro sequence screen as defined in the Game Class Diagram.
 * Displays introductory image, text, and initialises the Main Menu.
 */
>>>>>>> Stashed changes
public class Intro_screen implements Screen {
    private String textSize;
    private String text;
    private Texture image;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;

    public Intro_screen() {
        this.textSize = "16pt";
        this.text = "Tap or press any key to start";
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800, 480);

        if (Gdx.files != null && Gdx.files.internal("goose_intro.png").exists()) {
            this.image = new Texture("goose_intro.png");
        } else {
            this.image = null;
        }
    }

    /**
     * Renders the intro display sequence as defined in the Class Diagram (+display_intro()).
     */
    public void display_intro() {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        if (image != null) {
            batch.draw(image, 0, 0, 800, 480);
        } else {
            font.getData().setScale(2.5f);
            font.setColor(Color.GOLD);
            font.draw(batch, "GOOSE ESCAPE GAME", 220, 310);
            font.getData().setScale(1.2f);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Escape to Central Hall before time runs out!", 225, 240);
            font.getData().setScale(1.0f);
        }
        font.setColor(Color.WHITE);
        font.draw(batch, text, 305, 80);
        batch.end();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.10f, 0.16f, 1f);

        display_intro();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                dispose();
            }
        }
    }

    public String getTextSize() {
        return textSize;
    }

    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Texture getImage() {
        return image;
    }

    public void setImage(Texture image) {
        this.image = image;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (image != null) image.dispose();
    }
}
