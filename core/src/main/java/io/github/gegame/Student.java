package io.github.gegame;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.gegame.inventory.InitialInventory;
import io.github.gegame.inventory.Inventory;
import io.github.gegame.inventory.Item;

public class Student extends Player {

    private static final int MAX_HEALTH = 100;
    private static final double SPEED = 150; // pixels per second
    private static final int GOOSE_DAMAGE = 10;
    private static final double HIT_COOLDOWN = 1.0; // seconds of safety after goose attack
    private static final double UMBRELLA_SECONDS = 10.0;

    private double umbrellaTimer = 0; // seconds of umbrella protection left once activated
    private double hitCooldown = 0;   // seconds until the goose can hurt us again

    public Student(double x, double y) {
        super(x, y, 32, 32);
        this.maxHealth = MAX_HEALTH;
        this.health = MAX_HEALTH;
        new InitialInventory().populate(this.inventory);
        loadImage("images/player.png"); // Random path do change it!!
    }

    public void useSelectedItem(Goose goose) {
        Item item = inventory.getSelected();
        if (item != null && !item.isUsedUp()) {
            item.use(this, goose);
        }
    }

    public void update(double dt) {
        if (umbrellaTimer > 0) {
            umbrellaTimer = Math.max(0, umbrellaTimer - dt);
        }
        if (hitCooldown > 0) {
            hitCooldown = Math.max(0, hitCooldown - dt);
        }
    }

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

    public void moveLeft(double dt)  { x -= SPEED * dt; facingX = -1; facingY = 0; }
    public void moveRight(double dt) { x += SPEED * dt; facingX = 1;  facingY = 0; }
    public void moveUp(double dt)    { y += SPEED * dt; facingX = 0;  facingY = 1; }
    public void moveDown(double dt)  { y -= SPEED * dt; facingX = 0;  facingY = -1; }

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
