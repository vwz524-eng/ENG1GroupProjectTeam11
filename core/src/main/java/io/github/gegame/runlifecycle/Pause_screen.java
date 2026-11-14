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
 * Pause screen as defined in the Game Class Diagram.
 * Extends Retry and implements LibGDX Screen.
 */
public class Pause_screen extends Retry implements Screen {
    private String street;
    private String city;

    private SpriteBatch batch;
    private BitmapFont font;
    private FitViewport viewport;
    private Screen previousScreen;

    public Pause_screen() {
        this(null);
    }

    public Pause_screen(Screen previousScreen) {
        this.street = "Campus Way";
        this.city = "York";
        this.previousScreen = previousScreen;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800, 480);
    }

    public void Resume_Game() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            if (previousScreen != null) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(previousScreen);
            } else {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainGame());
            }
            dispose();
        }
    }

    public void Restart_Game() {
        Restart_game();
    }

    public void back_to_menu() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            dispose();
        }
    }

    @Override
    public void Restart_game() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainGame());
            dispose();
        }
    }

    @Override
    public void Restart_Score() {
        // Reset score on restart
    }

    @Override
    public void Reset_Level() {
        // Reset level on restart
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.08f, 0.12f, 0.95f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.GOLD);
        font.draw(batch, "=== GAME PAUSED ===", 320, 360);

        font.setColor(Color.WHITE);
        font.draw(batch, "Location: " + street + ", " + city, 300, 300);

        boolean colorBlind = Gdx.app != null
            && Gdx.app.getApplicationListener() instanceof Main
            && ((Main) Gdx.app.getApplicationListener()).isColorBlindMode();
        font.draw(batch, "[C] Colourblind Mode: " + (colorBlind ? "ON (High Contrast)" : "OFF"), 245, 260);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[P / ESC] Resume Game | [C] Colourblind | [R] Restart Game | [M] Main Menu", 150, 220);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Resume_Game();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Main) {
                ((Main) Gdx.app.getApplicationListener()).toggleColorBlindMode();
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            Restart_Game();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            back_to_menu();
        }
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
