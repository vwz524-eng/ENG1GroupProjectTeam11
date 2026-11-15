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

    public static final int MAX_HUNGER = 100;
    private static final int STARTING_HUNGER = 100;
    private static final double HUNGER_INCREASE_INTERVAL = 2.0;
    private static final int HUNGER_INCREASE_AMOUNT = 5;
    public static final int HIGH_HUNGER_THRESHOLD = 70;
    private static final double HIGH_HUNGER_SPEED_MULTIPLIER = 1.4;
    private static final int SEED_HUNGER_REDUCTION = 30;

    private int hunger = STARTING_HUNGER;
    private double hungerTimer = 0.0;
    private double baseSpeed = 90;
    private double speedMultiplier = 1.0;
    private int breadHungerReduction = 50; // Swarm 1: 2 crumbs; swarm 2: 4; swarm 3: 6
    private double lakeX, lakeY;
    private boolean returningToLake = false;
    private boolean backInLake = false;

    private double distractTimer = 0;
    private double distractX, distractY;
    private boolean eatingSeeds = false;
    private boolean seedHungerReduced = false;

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
        eatingSeeds = false;
        seedHungerReduced = false;
    }

    public void feed(int amount) {
        if (returningToLake || backInLake) return;
        eatTimer = 0.7; // short eating pose, then feeding can continue
        reduceHunger((int) Math.ceil(breadHungerReduction * Math.max(0, amount) / 10.0));
    }

    /** Where this goose came out of the lake, in the 640x480 gameplay coordinates. */
    public void setHomeLake(double x, double y) {
        lakeX = x;
        lakeY = y;
    }

    /** Swarm 1 needs 2 breadcrumbs, swarm 2 needs 4, swarm 3 needs 6. */
    public void setSwarm(int swarm) {
        breadHungerReduction = swarm == 1 ? 50 : (swarm == 2 ? 25 : 17);
    }

    public boolean isReturningToLake() { return returningToLake; }
    public boolean hasReturnedToLake() { return backInLake; }
    public boolean isAvailableForFeeding() { return !returningToLake && !backInLake; }

    public void reduceHunger(int amount) {
        if (amount > 0) {
            hunger = Math.max(0, hunger - amount);
            if (hunger == 0) {
                returningToLake = true;
                eatingSeeds = false;
                eatTimer = 0;
                distractTimer = 0;
            }
        }
    }

    public void increaseHunger(int amount) {
        if (amount > 0) {
            hunger = Math.min(MAX_HUNGER, hunger + amount);
        }
    }

    public int getHunger() { return hunger; }
    public int getMaxHunger() { return MAX_HUNGER; }
    public double getHungerPercent() { return (double) hunger / MAX_HUNGER; }
    public boolean isHighHunger() { return hunger >= HIGH_HUNGER_THRESHOLD; }

    public boolean isEating() { return eatTimer > 0 || eatingSeeds; }
    public boolean isDead() { return false; } // Existing API; geese leave rather than die
    public boolean isDistracted() { return distractTimer > 0; }

    public void update(double dt, Student student) {
        if (backInLake) return;

        if (returningToLake) {
            double dx = lakeX - centerX();
            double dy = lakeY - centerY();
            double distance = Math.hypot(dx, dy);
            if (distance < 5) {
                backInLake = true;
                updateAnimation(State.IDLE, dt);
            } else {
                double step = Math.min(baseSpeed * speedMultiplier * dt, distance);
                x += dx / distance * step;
                y += dy / distance * step;
                facing = Direction.fromVector(dx, dy, facing);
                updateAnimation(State.WALK, dt);
            }
            return;
        }

        if (eatTimer > 0) {
            eatTimer = Math.max(0, eatTimer - dt);
            eatingSeeds = false;
            updateAnimation(State.DISTRACTED, dt); // eating animation only while stationary
            return;
        }

        double targetX, targetY;
        if (distractTimer > 0) {
            distractTimer = Math.max(0, distractTimer - dt);
            targetX = distractX;
            targetY = distractY;
        } else {
            eatingSeeds = false;
            seedHungerReduced = false;
            targetX = student.centerX();
            targetY = student.centerY();
        }

        double dx = targetX - centerX();
        double dy = targetY - centerY();
        double dist = Math.hypot(dx, dy);

        State state;
        if (dist > 1.0) {
            double speed = getSpeed();
            double step = Math.min(speed * dt, dist);
            x += (dx / dist) * step;
            y += (dy / dist) * step;
            facing = Direction.fromVector(dx, dy, facing);

            double remainingDist = dist - step;
            if (distractTimer > 0 && remainingDist <= 1.0) {
                eatingSeeds = true;
                state = State.DISTRACTED;
            } else {
                eatingSeeds = false;
                state = distractTimer > 0 ? State.WALK : State.CHASE;
            }
        } else if (distractTimer > 0) {
            eatingSeeds = true;
            state = State.DISTRACTED; // arrived at seeds: stationary eating animation
        } else {
            eatingSeeds = false;
            state = State.IDLE;
        }

        if (eatingSeeds) {
            if (!seedHungerReduced) {
                reduceHunger(SEED_HUNGER_REDUCTION);
                seedHungerReduced = true;
            }
        } // Hunger only goes down when fed, keeping the planned 2/4/6 uses predictable.

        updateAnimation(state, dt);
    }

    // Textured drawing. There's no eating/dead art, so im doing them with a tint for now
    @Override
    public void draw(SpriteBatch batch) {
        if (batch == null) return;

        if (backInLake) return;
        if (isEating()) {
            batch.setColor(1f, 0.8f, 0.5f, 1f);
        }
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }

    // Fallback, used when texture couldnt be loaded
    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (backInLake) return;
        if (isEating()) {
            shapeRenderer.setColor(0.9f, 0.6f, 0.1f, 1f);
        } else if (isDistracted()) {
            shapeRenderer.setColor(1f, 1f, 0.2f, 1f);
        } else {
            shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 1f);
        }
        super.draw(shapeRenderer);
    }

    public void drawHungerBar(ShapeRenderer shapeRenderer, boolean colorBlindMode) {
        if (shapeRenderer == null || backInLake) {
            return;
        }
        float barWidth = 36f;
        float barHeight = 5f;
        float barX = (float) x - 2f;
        float barY = (float) Math.min(454.0, y + height + 4.0);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        shapeRenderer.rect(barX - 1f, barY - 1f, barWidth + 2f, barHeight + 2f);

        if (isHighHunger()) {
            shapeRenderer.setColor(colorBlindMode ? Color.MAGENTA : Color.RED);
        } else {
            shapeRenderer.setColor(colorBlindMode ? Color.CYAN : Color.ORANGE);
        }
        shapeRenderer.rect(barX, barY, barWidth * (float) getHungerPercent(), barHeight);
    }

    public double getSpeed() {
        double hungerMultiplier = isHighHunger() ? HIGH_HUNGER_SPEED_MULTIPLIER : 1.0;
        return baseSpeed * speedMultiplier * hungerMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = speedMultiplier > 0 ? speedMultiplier : 1.0;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }
}
