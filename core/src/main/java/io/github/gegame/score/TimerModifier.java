package io.github.gegame.score;

// Modifies the duration of the time based on difficulty/speed
public class TimerModifier {
    private float multiplier;

    public TimerModifier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1.0f;
    }

    public TimerModifier() {
        this(1.0f);
    }

    public int modify(int baseTime) {
        return Math.round(baseTime * multiplier);
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier > 0 ? multiplier : 1f;
    }
}

