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
 * Win screen as defined in the Game Class Diagram.
 * Extends Retry and implements LibGDX Screen.
 */
public class win_screen extends Retry implements Screen {
    private String title;
    private int Final_score;

    private SpriteBatch batch;
    private BitmapFont font;
    private FitViewport viewport;

    public win_screen() {
        this(0);
    }

    public win_screen(int finalScore) {
        this.title = "VICTORY - YOU WON!";
        this.Final_score = finalScore;

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
        return Final_score;
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
        this.Final_score = 0;
    }

    @Override
    public void Reset_Level() {
        // Reset level progression
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.2f, 0.08f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.GREEN);
        font.draw(batch, title, 320, 360);

        font.setColor(Color.WHITE);
        font.draw(batch, "Final Score: " + show_final_score(), 330, 300);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[R] Play Again | [M] Back to Menu", 280, 220);
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
        return Final_score;
    }

    public void setFinal_score(int final_score) {
        this.Final_score = final_score;
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
