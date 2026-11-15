package io.github.gegame.runlifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * Main Menu screen
*/
public class MainMenu implements Screen {
    private final String title;
    private final List<String> options;
    private int selectedoption;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;

    public MainMenu() {
        this.title = "A Day in the Life - Main Menu";
        this.options = new ArrayList<>(Arrays.asList("Start Game", "Exit"));
        this.selectedoption = 0;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800, 480);
    }

    public void start_game() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Main main) {
            main.startGameplay();
            dispose();
        } else if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainGame());
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.10f, 0.15f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.GOLD);
        font.draw(batch, title, 280, 400);

        for (int i = 0; i < options.size(); i++) {
            if (i == selectedoption) {
                font.setColor(Color.YELLOW);
                font.draw(batch, "> " + options.get(i), 320, 300 - (i * 40));
            } else {
                font.setColor(Color.WHITE);
                font.draw(batch, "  " + options.get(i), 320, 300 - (i * 40));
            }
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "Use UP/DOWN arrows to navigate, ENTER to select", 230, 100);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedoption = (selectedoption - 1 + options.size()) % options.size();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedoption = (selectedoption + 1) % options.size();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            selectOption(selectedoption);
        }
    }

    private void selectOption(int index) {
        switch (index) {
            case 0 -> start_game();
            case 1 -> Gdx.app.exit();
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
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }

}
