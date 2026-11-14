package io.github.gegame;

public class CountdownTimer {

    private int duration;
    private float remaining;
    private WarningLevel warningLevel;
    private RunState runState;

    public CountdownTimer() {
        duration = 300;
        remaining = duration;
        warningLevel = WarningLevel.NORMAL;
        runState = RunState.PAUSED;
    }

    public void start() {
        if (runState == RunState.PAUSED && remaining > 0) {
            runState = RunState.RUNNING;
        }
    }

    public void tick(float delta) {
        if (runState == RunState.RUNNING) {
            remaining = remaining - delta;

            if (remaining <= 0) {
                expire();
            }

            updateWarningLevel();
        }
    }

    public void pause() {
        if (runState == RunState.RUNNING) {
            runState = RunState.PAUSED;
        }
    }

    public void expire() {
        remaining = 0;
        runState = RunState.TIME_EXPIRED;
    }

    private void updateWarningLevel() {
        if (remaining <= 30) {
            warningLevel = WarningLevel.CRITICAL;
        } else if (remaining <= 120) {
            warningLevel = WarningLevel.WARNING;
        } else {
            warningLevel = WarningLevel.NORMAL;
        }
    }

    public int getRemaining() {
        return (int) Math.ceil(remaining);
    }

    public WarningLevel getWarningLevel() {
        return warningLevel;
    }

    public RunState getRunState() {
        return runState;
    }

    public void setRunState(RunState state) {
        if (runState == RunState.RUNNING) {
            if (state == RunState.ESCAPED ||
                state == RunState.CAUGHT) {
                runState = state;
            }
        }
    }
    // Display the remaining time as minutes and seconds
    public String getFormattedTime() {
        int minutes = getRemaining() / 60;
        int seconds = getRemaining() % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}
