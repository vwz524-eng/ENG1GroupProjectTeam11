package io.github.gegame.interaction;

//Represents an active or triggered distraction in the world

public class Distraction {
    private final double radius;
    private boolean active;

    public Distraction(double radius) {
        this.radius = Math.max(0.0, radius);
        this.active = false;
    }

    //Activates this distraction

    public void trigger() {
        this.active = true;
    }

    //Deactivates

    public void dismiss() {
        this.active = false;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isActive() {
        return active;
    }
}

