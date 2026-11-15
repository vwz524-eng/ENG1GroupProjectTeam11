package io.github.gegame.runlifecycle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.gegame.Main;
import io.github.gegame.score.DifficultyLevel;

/**
 * Intro sequence screen as defined in the Game Class Diagram
 */
public class Intro_screen implements Screen {
    private final String text;
    private Texture image;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;

    private static final DifficultyLevel[] DIFFICULTIES = {
        DifficultyLevel.EASY,
        DifficultyLevel.NORMAL,
        DifficultyLevel.HARD
    };
    private int difficultyIndex = 1;

    public Intro_screen() {
        this.text = "Press ENTER / SPACE or click to continue";
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800, 480);

        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Main) {
            DifficultyLevel current = ((Main) Gdx.app.getApplicationListener()).getDifficulty();
            for (int i = 0; i < DIFFICULTIES.length; i++) {
                if (DIFFICULTIES[i].getName().equalsIgnoreCase(current.getName())) {
                    difficultyIndex = i;
                    break;
                }
            }
        }

        if (Gdx.files != null && Gdx.files.internal("lifecycle_assets/goose_intro.png").exists()) {
            this.image = new Texture("lifecycle_assets/goose_intro.png");
        } else if (Gdx.files != null && Gdx.files.internal("goose_intro.png").exists()) {
            this.image = new Texture("goose_intro.png");
        } else {
            this.image = null;
        }
    }

    //Renders the intro display sequenc
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
        DifficultyLevel selected = DIFFICULTIES[difficultyIndex];
        font.setColor(Color.GOLD);
        font.draw(batch,
            "Difficulty: < " + selected.getName().toUpperCase() + " > ("
                + selected.getBaseMultiplier() + "x score, "
                + selected.getGooseSpeedModifier().multiplier() + "x goose speed)",
            195, 110);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[LEFT / RIGHT or TAB or 1-3] Change Difficulty", 240, 82);
        font.setColor(Color.WHITE);
        font.draw(batch, text, 265, 52);
        batch.end();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.10f, 0.16f, 1f);

        display_intro();

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)
                || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)
                || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            setDifficultyIndex((difficultyIndex + 1) % DIFFICULTIES.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)
                || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            setDifficultyIndex((difficultyIndex - 1 + DIFFICULTIES.length) % DIFFICULTIES.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            setDifficultyIndex(0);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            setDifficultyIndex(1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            setDifficultyIndex(2);
        } else if (Gdx.input.justTouched()
                || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Main) {
                ((Main) Gdx.app.getApplicationListener()).setDifficulty(DIFFICULTIES[difficultyIndex]);
            }
            if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
                dispose();
            }
        }
    }

    private void setDifficultyIndex(int index) {
        difficultyIndex = index;
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Main) {
            ((Main) Gdx.app.getApplicationListener()).setDifficulty(DIFFICULTIES[difficultyIndex]);
        }
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
