package io.github.gegame.inventory;

import io.github.gegame.Goose;
import io.github.gegame.Player;

//Bread feed item, fed to geese to reduce their hunger on a 10-second cooldown.

public class Bread extends FeedItem {
    public static final double COOLDOWN_SECONDS = 0.00;
    private double cooldownTimer = 0.0;

    public Bread() {
        super("bread", "Bread", "Feed a goose to reduce hunger (10s cooldown)", 10);
    }

    public void updateCooldown(double dt) {
        if (cooldownTimer > 0 && dt > 0) {
            cooldownTimer = Math.max(0.0, cooldownTimer - dt);
        }
    }

    public boolean isOnCooldown() {
        return cooldownTimer > 0.0;
    }

    public double getCooldownRemaining() {
        return cooldownTimer;
    }

    public void startCooldown() {
        this.cooldownTimer = COOLDOWN_SECONDS;
    }

    @Override
    public void use(Player player) {
        if (!isOnCooldown()) {
            startCooldown();
        }
    }

    @Override
    public void use(Player player, Goose goose) {
        if (isOnCooldown()) {
            return;
        }
        if (goose != null) {
            goose.feed(getFeedAmount());
        }
        startCooldown();
    }
}

