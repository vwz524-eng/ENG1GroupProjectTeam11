package io.github.gegame.escape;

/**
 * Manages the countdown timer, tracks warning levels, and monitors run state.
 */
public class CountdownTimer {
    public static final int DEFAULT_DURATION = 300; // 5 minutes in seconds
    public static final int WARNING_THRESHOLD = 120; // 2 minutes remaining
    public static final int CRITICAL_THRESHOLD = 30; // 30 seconds remaining

    private int duration;
    private int remaining;
    private WarningLevel warningLevel;
    private RunState runState;

    private float accumulatedDelta = 0f;

    public CountdownTimer() {
        this(DEFAULT_DURATION);
    }

    public CountdownTimer(int duration) {
        this.duration = Math.max(1, duration);
        this.remaining = this.duration;
        this.warningLevel = WarningLevel.NORMAL;
        this.runState = RunState.PAUSED;
        updateWarningLevel();
    }

    /**
     * Starts or resumes the countdown timer.
     */
    public void start() {
        if (!isExpired()) {
            this.runState = RunState.RUNNING;
        }
    }

    /**
     * Pauses the countdown timer without losing progress.
     */
    public void pause() {
        if (this.runState != null && this.runState.isRunning()) {
            this.runState = RunState.PAUSED;
        }
    }

    /**
     * Ticks the timer by 1 second.
     */
    public void tick() {
        if (this.runState == null || !this.runState.isRunning()) {
            return;
        }
        if (remaining > 0) {
            remaining--;
            updateWarningLevel();
            if (remaining <= 0) {
                expire();
            }
        }
    }

    /**
     * Ticks the timer by elapsed delta time in seconds.
     *
     * @param dt delta time in seconds
     */
    public void tick(float dt) {
        if (this.runState == null || !this.runState.isRunning()) {
            return;
        }
        accumulatedDelta += dt;
        while (accumulatedDelta >= 1.0f) {
            accumulatedDelta -= 1.0f;
            tick();
        }
    }

    /**
     * Expires the timer immediately, transitioning to TIME_EXPIRED state.
     */
    public void expire() {
        this.remaining = 0;
        this.warningLevel = WarningLevel.CRITICAL;
        this.runState = RunState.TIME_EXPIRED;
    }

    public boolean isExpired() {
        return remaining <= 0 || runState instanceof TimeExpiredState || RunState.TIME_EXPIRED.equals(runState);
    }

    public void reset() {
        this.remaining = this.duration;
        this.runState = RunState.PAUSED;
        this.accumulatedDelta = 0f;
        updateWarningLevel();
    }

    private void updateWarningLevel() {
        if (remaining <= CRITICAL_THRESHOLD) {
            this.warningLevel = WarningLevel.CRITICAL;
        } else if (remaining <= WARNING_THRESHOLD) {
            this.warningLevel = WarningLevel.WARNING;
        } else {
            this.warningLevel = WarningLevel.NORMAL;
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = Math.max(1, duration);
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = Math.max(0, remaining);
        updateWarningLevel();
    }

    public WarningLevel getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(WarningLevel warningLevel) {
        this.warningLevel = warningLevel;
    }

    public RunState getRunState() {
        return runState;
    }

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    /**
     * Formats the remaining time as MM:SS.
     *
     * @return formatted time string
     */
    public String getFormattedTime() {
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

