package io.github.gegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import io.github.gegame.animation.AnimState;
import io.github.gegame.animation.Direction;
import io.github.gegame.animation.SpriteAnimator;

public abstract class Sprite {
    protected double x, y;
    protected int width, height;
    protected Texture texture;

    // animation support, remains null for sprites that don't call/use initAnimator()
    protected SpriteAnimator animator;
    // which way the sprite faces, subclasses update this as they move
    protected Direction facing = Direction.DOWN;

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
     * Turns on directional sprite-sheet animation for this entity.
     * Call from the subclass constructor, then chain register(...) for each state:
     * <pre>
     *   initAnimator("geese", "goose")
     *       .register(State.IDLE, SpriteAnimator.AUTO, 0.2f, true);
     * </pre>
     *
     * @param folder folder inside assets/ (e.g. "geese")
     * @param prefix start of every filename in that folder (e.g. "goose")
     */
    protected SpriteAnimator initAnimator(String folder, String prefix) {
        animator = new SpriteAnimator(folder, prefix);
        return animator;
    }

    /**
     * Picks the animation for the given state (using the current {@link #facing})
     * and advances it. Call once per update.
     */
    protected void updateAnimation(AnimState state, double dt) {
        if (animator == null) return;
        animator.play(state, facing);
        animator.update(dt);
    }

    /**
     * Draws the current animation frame if there is one, otherwise the static texture.
     *
     * @param batch the active SpriteBatch
     */
    public void draw(SpriteBatch batch) {
        if (batch == null) return;

        TextureRegion frame = animator != null ? animator.getFrame() : null;
        if (frame != null) {
            batch.draw(frame, (float) x, (float) y, (float) width, (float) height);
        } else if (texture != null) {
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
        if (animator != null) {
            animator.dispose();
            animator = null;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Texture getTexture() { return texture; }
    public Direction getFacing() { return facing; }

    /** True if there is anything to draw with the SpriteBatch (static texture or loaded animations). */
    public boolean hasTexture() {
        return texture != null || (animator != null && animator.hasFrames());
    }
}
