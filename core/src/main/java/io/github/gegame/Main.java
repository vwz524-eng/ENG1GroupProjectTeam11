package io.github.gegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        
        //space -> Shoot
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            player.shoot();
        }
        //WASD -> Move
        player.move(
				Gdx.input.isKeyPressed(Input.Keys.A),
				Gdx.input.isKeyPressed(Input.Keys.D),
				Gdx.input.isKeyPressed(Input.Keys.S),
				Gdx.input.isKeyPressed(Input.Keys.W), 
				Gdx.graphics.getDeltaTime());
				
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
