package io.github.gegame.runlifecycle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Intro_screen implements Screen {
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Texture introImage;
    private final FitViewport viewport;

    public Intro_screen() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        introImage = new Texture("goose_intro.png");
        viewport = new FitViewport(800, 480);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(introImage, 0, 0, 800, 480);
        font.draw(batch, "Tap or press any key to start", 310, 40);
        batch.end();

//        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
//            ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
//            dispose();
//        }
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
        introImage.dispose();
    }
}