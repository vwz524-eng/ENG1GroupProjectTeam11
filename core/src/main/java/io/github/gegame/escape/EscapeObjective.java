package io.github.gegame.escape;

/**
 * Represents the player's escape goal (e.g. reaching Central Hall).
 */
public class EscapeObjective {
    private boolean required;
    private boolean completed;

    public EscapeObjective() {
        this(true);
    }

    public EscapeObjective(boolean required) {
        this.required = required;
        this.completed = false;
    }

    /**
     * Marks this escape objective as completed.
     */
    public void complete() {
        this.completed = true;
    }

    /**
     * Checks if the objective is satisfied.
     *
     * @return true if completed or not required, false otherwise
     */
    public boolean isSatisfied() {
        if (!required) {
            return true;
        }
        return completed;
    }

    /**
     * Evaluates whether the objective can still be completed with the given timer.
     *
     * @param timer the countdown timer
     * @return true if remaining time > 0 and timer has not expired
     */
    public boolean canBeCompleted(CountdownTimer timer) {
        if (timer == null) return false;
        return timer.getRemaining() > 0 && !timer.isExpired();
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

