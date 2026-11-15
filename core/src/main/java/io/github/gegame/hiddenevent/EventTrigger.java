package io.github.gegame.hiddenevent;

//Triggers a hidden event check based on timing thresholds

public class EventTrigger {
    private int timeThreshold;
    private double elapsedTime;

    public EventTrigger(int timeThreshold) {
        this.timeThreshold = timeThreshold;
        this.elapsedTime = 0;
    }

    public EventTrigger() {
        this(0);
    }

    public void update(double dt) {
        this.elapsedTime += dt;
    }

    public boolean check() {
        return elapsedTime >= timeThreshold;
    }

    public int getTimeThreshold() {
        return timeThreshold;
    }

    public void setTimeThreshold(int timeThreshold) {
        this.timeThreshold = timeThreshold;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void reset() {
        this.elapsedTime = 0;
    }
}

