package io.github.gegame.interaction;

//Represents an active or triggered distraction in the world

public class Distraction {
    private double radius;
    private boolean active;

    public Distraction() {
        this(150.0);
    }

    public Distraction(double radius) {
        this.radius = Math.max(0.0, radius);
        this.active = false;
    }

    public Distraction(double radius, boolean active) {
        this.radius = Math.max(0.0, radius);
        this.active = active;
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

    public void setRadius(double radius) {
        this.radius = Math.max(0.0, radius);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

