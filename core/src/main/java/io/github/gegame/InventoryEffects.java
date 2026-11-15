package io.github.gegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import io.github.gegame.animation.Direction;
import io.github.gegame.hiddenevent.AllergyMedicationItem;
import io.github.gegame.inventory.Bread;
import io.github.gegame.inventory.DistractionItem;
import io.github.gegame.inventory.Item;

public class InventoryEffects {
    private final Texture crumbs, seeds, medication;
    private final Texture umbrellaUp, umbrellaDown, umbrellaLeft, umbrellaRight;
    private Texture throwEffect;
    private float throwEffectSeconds;
    private float medicationSeconds;
    private float effectX, effectY;

    public InventoryEffects() {
        crumbs = load("inventory/effects/breadcrumbs_scattered.png");
        seeds = load("inventory/effects/birdseed_scattered.png");
        medication = load("inventory/allergy_medication.png");
        umbrellaUp = load("inventory/effects/umbrella_open_up.png");
        umbrellaDown = load("inventory/effects/umbrella_open_down.png");
        umbrellaLeft = load("inventory/effects/umbrella_open_left.png");
        umbrellaRight = load("inventory/effects/umbrella_open_right.png");
    }

    private Texture load(String path) {
        if (Gdx.files.internal(path).exists()) {
            return new Texture(Gdx.files.internal(path));
        }
        Gdx.app.error("InventoryEffects", "Missing asset: " + path);
        return null;
    }

    /** Call only after Student reports a successful item use. */
    public void trigger(Item item, Student student) {
        if (item instanceof Bread || item instanceof DistractionItem) {
            throwEffect = item instanceof Bread ? crumbs : seeds;
            effectX = (float) (student.centerX() + student.getFacingX() * 45 - 16);
            effectY = (float) (student.centerY() + student.getFacingY() * 45 - 16);
            throwEffectSeconds = 0.75f;
        } else if (item instanceof AllergyMedicationItem) {
            medicationSeconds = 0.85f;
        }
    }

    public void reset() {
        throwEffectSeconds = 0f;
        medicationSeconds = 0f;
    }

    public void update(float dt) {
        throwEffectSeconds = Math.max(0f, throwEffectSeconds - dt);
        medicationSeconds = Math.max(0f, medicationSeconds - dt);
    }

    public void render(SpriteBatch batch, Student student) {
        if (throwEffectSeconds > 0 && throwEffect != null) {
            batch.draw(throwEffect, effectX, effectY, 32f, 32f);
        }
        if (medicationSeconds > 0 && medication != null) {
            // Briefly shown beside the hand, facing the same direction as the player.
            float mx = (float) (student.centerX() + student.getFacingX() * 17 - 12);
            float my = (float) (student.centerY() + student.getFacingY() * 17 - 12);
            batch.draw(medication, mx, my, 24f, 24f);
        }
        if (student.hasUmbrella()) {
            Direction d = student.getFacing();
            Texture t = d == Direction.UP ? umbrellaUp : d == Direction.DOWN ? umbrellaDown
                : d == Direction.LEFT ? umbrellaLeft : umbrellaRight;
            if (t != null) {
                float x = (float) student.getX();
                float y = (float) student.getY();
                switch (d) {
                    case UP -> { x += 16; y += 42; }
                    case DOWN -> { x += 16; y += 8; }
                    case LEFT -> { x -= 15; y += 25; }
                    default -> { x += 46; y += 25; } // RIGHT
                }
                batch.draw(t, x, y, 32f, 32f); // replace magic numbes?
            }
        }
    }

    public void dispose() {
        for (Texture t : new Texture[] { crumbs, seeds, medication,
            umbrellaUp, umbrellaDown, umbrellaLeft, umbrellaRight }) {
            if (t != null) t.dispose();
        }
    }
}
