package io.github.gegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.gegame.animation.AnimState;
import io.github.gegame.animation.Direction;
import io.github.gegame.animation.SpriteAnimator;

public class Goose extends Sprite {

    /**
     * Animation states. The 'token' is the part of the filename between "goose"  "$direction"
     */
    public enum State implements AnimState {
        IDLE(""), // geese/goose_<dir>.png
        WALK("walk"), // geese/goose_walk_<dir>.png, registered, not used by the AI!!
        CHASE("chase"), // geese/goose_chase_<dir>.png
        DISTRACTED("distracted");  // geese/goose_distracted_<dir>.png

        private final String token;

        State(String token) { this.token = token; }

        @Override
        public String fileToken() { return token; }
    }

    private int health = 30;
    private double baseSpeed = 90;
    private double speedMultiplier = 1.0;

    private double distractTimer = 0;
    private double distractX, distractY;

    private double eatTimer = 0; // stops to eat after being fed bread

    public Goose(double x, double y) {
        super(x, y, 32, 32);

        // frameCount AUTO = frame width equals image height, so automatically divides into how many frames
        initAnimator("geese", "goose")
                .register(State.IDLE,       SpriteAnimator.AUTO, 0.25f, true)
                .register(State.WALK,       SpriteAnimator.AUTO, 0.15f, true)
                .register(State.CHASE,      SpriteAnimator.AUTO, 0.10f, true)
                .register(State.DISTRACTED, SpriteAnimator.AUTO, 0.20f, true);

        facing = Direction.LEFT; // spawns on the right, looking toward the student
    }

    public void distract(double targetX, double targetY, double seconds) {
        distractX = targetX;
        distractY = targetY;
        distractTimer = seconds;
    }

    public void feed(int damage) {
        health = health - damage;
        eatTimer = 2.0;
    }

    public boolean isEating() { return eatTimer > 0; }
    public boolean isDead() { return health <= 0; }
    public boolean isDistracted() { return distractTimer > 0; }

    public void update(double dt, Student student) {
        if (isDead()) {
            updateAnimation(State.IDLE, dt);
            return;
        }

        if (eatTimer > 0) {
            eatTimer -= dt;
            updateAnimation(State.IDLE, dt); // stopped, keeps facing the way it was going
            return;
        }

        double targetX, targetY;
        State state;
        if (distractTimer > 0) {
            distractTimer -= dt;
            targetX = distractX;
            targetY = distractY;
            state = State.DISTRACTED;
        } else {
            targetX = student.centerX();
            targetY = student.centerY();
            state = State.CHASE;
        }

        double dx = targetX - centerX();
        double dy = targetY - centerY();
        double dist = Math.hypot(dx, dy);
        if (dist > 1) {
            double speed = getSpeed();
            x += (dx / dist) * speed * dt;
            y += (dy / dist) * speed * dt;
            facing = Direction.fromVector(dx, dy, facing);
        }

        updateAnimation(state, dt);
    }

    // Textured drawing. There's no eating/dead art, so im doing them with a tint for now
    @Override
    public void draw(SpriteBatch batch) {
        if (batch == null) return;

        if (isDead()) {
            batch.setColor(0.4f, 0.4f, 0.4f, 1f);
        } else if (isEating()) {
            batch.setColor(1f, 0.8f, 0.5f, 1f);
        }
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }

    // Fallback, used when texture couldnt be loaded
    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (isDead()) {
            shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
        } else if (isEating()) {
            shapeRenderer.setColor(0.9f, 0.6f, 0.1f, 1f);
        } else if (isDistracted()) {
            shapeRenderer.setColor(1f, 1f, 0.2f, 1f);
        } else {
            shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 1f);
        }
        super.draw(shapeRenderer);
    }

    public double getSpeed() {
        return baseSpeed * speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier > 0 ? speedMultiplier : 1.0;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
