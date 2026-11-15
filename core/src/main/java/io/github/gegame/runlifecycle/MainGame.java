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

/**
 * Main Game state / screen representation as defined in the Game Class Diagram.
 * Tracks score, level, lives, and triggers win, lose, and pause screens.
 */
public class MainGame implements Screen {
    private Integer score;
    private Integer level;
    private Integer lives;

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final FitViewport viewport;

    public MainGame() {
        this.score = 0;
        this.level = 1;
        this.lives = 3;

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.viewport = new FitViewport(800, 480);
    }

    public void updatePrice() {
        // Corresponds to +updatePrice() from the UML Class Diagram
        this.score += 100;
    }

    public void triggerLose() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new lose_screen(score));
            dispose();
        }
    }

    public void triggerWin() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new win_screen(score));
            dispose();
        }
    }

    public void triggerPause() {
        if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof Game) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new Pause_screen());
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.12f, 0.15f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "=== MAIN GAME ===", 320, 420);
        font.draw(batch, "Score: " + score, 100, 350);
        font.draw(batch, "Level: " + level, 100, 320);
        font.draw(batch, "Lives: " + lives, 100, 290);

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[P] Pause | [W] Trigger Win | [L] Trigger Lose | [U] Update Score/Price", 180, 150);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            triggerPause();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            triggerWin();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            triggerLose();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            updatePrice();
        }
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLives() {
        return lives;
    }

    public void setLives(Integer lives) {
        this.lives = lives;
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

