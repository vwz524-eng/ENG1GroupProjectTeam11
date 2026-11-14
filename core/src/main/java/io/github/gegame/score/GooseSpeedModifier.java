package io.github.gegame.score;

/**
 * Modifies goose movement speed according to difficulty level and swarm tier.
 */
public class GooseSpeedModifier {
    private float multiplier;

    public GooseSpeedModifier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1.0f;
    }

    public GooseSpeedModifier() {
        this(1.0f);
    }

    public float modify(float baseSpeed) {
        return baseSpeed * multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1.0f;
    }
}

