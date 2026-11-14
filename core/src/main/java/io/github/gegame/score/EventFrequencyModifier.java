package io.github.gegame.score;

/**
 * Modifies the frequency or probability of game events according to difficulty level.
 */
public class EventFrequencyModifier {
    private float multiplier;

    public EventFrequencyModifier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1.0f;
    }

    public EventFrequencyModifier() {
        this(1.0f);
    }

    public float modify(float baseFrequency) {
        return baseFrequency * multiplier;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1.0f;
    }
}

