package io.github.gegame.interaction;

// Regulates the timing between interactions.

public class Cooldown {
    private final double duration;
    private double remainingTime;

    public Cooldown(double duration) {
        this.duration = Math.max(0.0, duration);
        this.remainingTime = 0.0;
    }

    //Starts the cooldown timer by setting remainingTime to duration

    public void start() {
        this.remainingTime = this.duration;
    }

    /**
    Updates the remaining time based on elapsed delta time
    @param deltaTime the elapsed time in seconds
     */

    public void update(double deltaTime) {
        if (remainingTime > 0) {
            remainingTime = Math.max(0.0, remainingTime - deltaTime);
        }
    }

    /**
    Checks if the cooldown has elapsed and the action is ready to execute
    @return true if remainingTime is less than 0, false otherwise
     */

    public boolean isReady() {
        return remainingTime <= 0.0;
    }

    public double getRemainingTime() {
        return remainingTime;
    }

    public void reset() {
        this.remainingTime = 0.0;
    }
}

