package io.github.gegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.gegame.animation.AnimState;
import io.github.gegame.animation.Direction;
import io.github.gegame.animation.SpriteAnimator;
import io.github.gegame.inventory.DistractionItem;
import io.github.gegame.inventory.FeedItem;
import io.github.gegame.inventory.InitialInventory;
import io.github.gegame.inventory.Inventory;
import io.github.gegame.inventory.Item;

public class Student extends Player {

    /**
     * Animation states. The token is the part of the filename between "student" and the direction.
     */
    public enum State implements AnimState {
        IDLE(""),        // characters/student_<dir>.png
        WALK("walk"),    // characters/student_walk_<dir>.png
        THROW("throw");  // characters/student_throw_<dir>.png

        private final String token;

        State(String token) { this.token = token; }

        @Override
        public String fileToken() { return token; }
    }

    private static final int MAX_HEALTH = 100;
    private static final double SPEED = 150; // pixels per second
    private static final int GOOSE_DAMAGE = 10;
    private static final double HIT_COOLDOWN = 1.0; // seconds of safety after goose attack
    private static final double UMBRELLA_SECONDS = 10.0;
    private static final double THROW_SECONDS = 0.35; // how long the throw pose is held

    private double umbrellaTimer = 0; // seconds of umbrella protection left once activated
    private double hitCooldown = 0;   // seconds until the goose can hurt us again
    private double throwTimer = 0;    // seconds left of the throw animation
    private boolean movedThisFrame = false;

    public Student(double x, double y) {
        super(x, y, 64, 64);
        this.maxHealth = MAX_HEALTH;
        this.health = MAX_HEALTH;
        new InitialInventory().populate(this.inventory);

        // frameCount AUTO = "frame width equals image height"; pass a number if your sheets differ.
        initAnimator("characters", "student")
                .register(State.IDLE,  SpriteAnimator.AUTO, 0.25f, true)
                .register(State.WALK,  SpriteAnimator.AUTO, 0.12f, true)
                .register(State.THROW, SpriteAnimator.AUTO, 0.10f, false);
    }

    public void useSelectedItem(Goose goose) {
        Item item = inventory.getSelected();
        if (item != null && !item.isUsedUp()) {
            item.use(this, goose);
            if (item instanceof DistractionItem || item instanceof FeedItem) {
                throwTimer = THROW_SECONDS; // thrown items get the throw pose; the umbrella doesn't
            }
        }
    }

    public void update(double dt) {
        if (umbrellaTimer > 0) {
            umbrellaTimer = Math.max(0, umbrellaTimer - dt);
        }
        if (hitCooldown > 0) {
            hitCooldown = Math.max(0, hitCooldown - dt);
        }
        if (throwTimer > 0) {
            throwTimer = Math.max(0, throwTimer - dt);
        }

        // Pick the animation: throw beats walk beats idle.
        facing = Direction.fromVector(facingX, facingY, facing);
        State state = throwTimer > 0 ? State.THROW
                : movedThisFrame ? State.WALK
                : State.IDLE;
        movedThisFrame = false; // move*() sets it again next frame if a key is still held
        updateAnimation(state, dt);
    }

    /** Textured drawing. Tints replace the old colour changes for umbrella / just-hit feedback. */
    @Override
    public void draw(SpriteBatch batch) {
        if (batch == null) return;

        if (hasUmbrella()) {
            batch.setColor(0.6f, 0.85f, 1f, 1f);
        } else if (hitCooldown > 0) {
            batch.setColor(1f, 0.5f, 0.5f, 1f);
        }
        super.draw(batch);
        batch.setColor(Color.WHITE);
    }

    /** Fallback box, only used when no textures loaded. */
    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        if (hasUmbrella()) {
            shapeRenderer.setColor(0.3f, 0.7f, 1f, 1f);
        } else if (hitCooldown > 0) {
            shapeRenderer.setColor(1f, 0.4f, 0.4f, 1f);
        } else {
            shapeRenderer.setColor(0.2f, 0.8f, 0.3f, 1f);
        }
        super.draw(shapeRenderer);
    }

    @Override
    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount);
    }

    public void giveUmbrella() {
        umbrellaTimer = UMBRELLA_SECONDS;
    }

    public boolean hasUmbrella() {
        return umbrellaTimer > 0;
    }

    public void checkGooseCollision(Goose goose) {
        if (collidesWith(goose)) {
            takeDamage(GOOSE_DAMAGE);
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (hasUmbrella() || hitCooldown > 0) return; // protected, nothing happens
        health = health - amount;
        hitCooldown = HIT_COOLDOWN; // so one touch doesn't hurt every frame
    }

    public void moveLeft(double dt)  { x -= SPEED * dt; facingX = -1; facingY = 0;  movedThisFrame = true; }
    public void moveRight(double dt) { x += SPEED * dt; facingX = 1;  facingY = 0;  movedThisFrame = true; }
    public void moveUp(double dt)    { y += SPEED * dt; facingX = 0;  facingY = 1;  movedThisFrame = true; }
    public void moveDown(double dt)  { y -= SPEED * dt; facingX = 0;  facingY = -1; movedThisFrame = true; }

    // Getters and Setters
    @Override
    public int getHealth() { return health; }
    @Override
    public int getMaxHealth() { return MAX_HEALTH; }
    public double getHealthPercent() { return (double) health / MAX_HEALTH; } // for the health bar
    @Override
    public void setHealth(int health) { this.health = Math.max(0, Math.min(MAX_HEALTH, health)); }
    @Override
    public boolean isDead() { return health <= 0; }

    public double getUmbrellaTimeLeft() { return umbrellaTimer; }
    public double getSpeed() { return SPEED; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }

    @Override
    public Inventory getInventory() { return inventory; }
    @Override
    public double getFacingX() { return facingX; }
    @Override
    public double getFacingY() { return facingY; }
}
