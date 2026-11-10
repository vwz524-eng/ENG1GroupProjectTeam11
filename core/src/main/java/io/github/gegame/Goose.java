package io.github.gegame;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Goose extends Sprite {
    private int health = 30;
    private final double speed = 90;
 
    private double distractTimer = 0;
    private double distractX, distractY;
 
    private double eatTimer = 0; // stops to eat after being fed bread
 
    public Goose(double x, double y) {
        super(x, y, 40, 40);
        loadImage("images/goose.png");
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
        if (isDead()) return;
 
        if (eatTimer > 0) {
            eatTimer -= dt;
            return;
        }
 
        double targetX, targetY;
        if (distractTimer > 0) {
            distractTimer -= dt;
            targetX = distractX;
            targetY = distractY;
        } else {
            targetX = student.centerX();
            targetY = student.centerY();
        }
 
        double dx = targetX - centerX();
        double dy = targetY - centerY();
        double dist = Math.hypot(dx, dy);
        if (dist > 1) {
            x += (dx / dist) * speed * dt;
            y += (dy / dist) * speed * dt;
        }
    }

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
}
