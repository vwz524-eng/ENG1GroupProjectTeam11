package io.github.gegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public abstract class Sprite {
    protected double x, y;
    protected int width, height;
    protected Texture texture;

    public Sprite(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Loads a libGDX texture from internal assets if available.
     * Safe to call in headless test environments.
     *
     * @param path path relative to assets folder
     */
    protected void loadImage(String path) {
        try {
            if (Gdx.files != null && Gdx.files.internal(path).exists()) {
                this.texture = new Texture(Gdx.files.internal(path));
            }
        } catch (Exception e) {
            System.out.println("Could not load texture: " + path);
        }
    }

    /**
     * Draws the sprite texture using SpriteBatch.
     *
     * @param batch the active SpriteBatch
     */
    public void draw(SpriteBatch batch) {
        if (texture != null && batch != null) {
            batch.draw(texture, (float) x, (float) y, (float) width, (float) height);
        }
    }

    /**
     * Draws the sprite shape using ShapeRenderer (fallback or debug).
     *
     * @param shapeRenderer the active ShapeRenderer
     */
    public void draw(ShapeRenderer shapeRenderer) {
        if (shapeRenderer != null) {
            shapeRenderer.rect((float) x, (float) y, (float) width, (float) height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((float) x, (float) y, (float) width, (float) height);
    }

    public boolean collidesWith(Sprite other) {
        if (other == null) return false;
        return getBounds().overlaps(other.getBounds());
    }

    public double centerX() {
        return x + width / 2.0;
    }

    public double centerY() {
        return y + height / 2.0;
    }

    public double distanceTo(Sprite other) {
        return Math.hypot(centerX() - other.centerX(), centerY() - other.centerY());
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Texture getTexture() { return texture; }
    public boolean hasTexture() { return texture != null; }
}
