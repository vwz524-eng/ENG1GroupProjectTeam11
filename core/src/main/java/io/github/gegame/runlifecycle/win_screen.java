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
 * Win screen
*/
public class win_screen extends Retry implements Screen {
    private final String title;
    private final int Final_score;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;

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
        batch.dispose();
        font.dispose();
    }
}
