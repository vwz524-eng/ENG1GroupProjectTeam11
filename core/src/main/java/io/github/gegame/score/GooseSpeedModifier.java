package io.github.gegame.score;

/**
 * Changes goose speed according to difficulty
 */
public record GooseSpeedModifier(float multiplier) {
    public GooseSpeedModifier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1.0f;
    }

    public GooseSpeedModifier() {
        this(1.0f);
    }

    public float modify(float baseSpeed) {
        return baseSpeed * multiplier;
    }

}

