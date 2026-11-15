package io.github.gegame.runlifecycle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import io.github.gegame.Main;

/**
 * Lose screen
 * Extends Retry and implements LibGDX Screen
 */
public class lose_screen extends Retry implements Screen {
    private String title;
    private int final_score;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;

    public lose_screen() {
        this(0);
    }

    public lose_screen(int finalScore) {
        this.title = "GAME OVER - YOU LOSE";
        this.final_score = finalScore;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800, 480);
    }

    public void Retry_game() {
        Restart_game();
    }

    public void Back_to_menu() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            dispose();
        }
    }

    public int show_final_score() {
        return final_score;
    }

    @Override
    public void Restart_game() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Main) {
            ((Main) Gdx.app.getApplicationListener()).returnToIntroScreen();
            dispose();
        } else if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Intro_screen());
            dispose();
        }
    }

    @Override
    public void Restart_Score() {
        this.final_score = 0;
    }

    @Override
    public void Reset_Level() {}

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.05f, 0.05f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.RED);
        font.draw(batch, title, 300, 360);

        font.setColor(Color.WHITE);
        font.draw(batch, "Final Score: " + show_final_score(), 330, 300);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[R] Retry Game | [M] Back to Menu", 280, 220);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Retry_game();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            Back_to_menu();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getFinal_score() {
        return final_score;
    }

    public void setFinal_score(int final_score) {
        this.final_score = final_score;
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
    }
}
