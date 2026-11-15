package io.github.gegame;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public abstract class Entity {
    protected Texture texture;
    protected Rectangle rectangle;
    protected World world;
    protected float angle = 0.0f;


    public Entity(World world) {
        this.world = world;
        world.getEntities().add(this);
    }

    public final Texture getTexture() {
        if (texture == null) {
            texture = createTexture();
        }
        return texture;
    }

    public abstract Texture createTexture();

    public float getAngle() {
        return angle;
    }

    public float getX() {
        return rectangle.getX();
    }

    public float getY() {
        return rectangle.getY();
    }

    public float getWidth() {
        return rectangle.width;
    }

    public float getHeight() {
        return rectangle.height;
    }


    public void draw(SpriteBatch batch) {


        if (batch != null) {
            batch.draw(getTexture(), getX(), getY(),
                getWidth() / 2, getWidth() / 2,
                getWidth(), getHeight(),
                1.0f, 1.0f, getAngle(),
                0, 0,
                (int) getWidth(), (int) getHeight(),
                false, false);
        }
    }


    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }


}
