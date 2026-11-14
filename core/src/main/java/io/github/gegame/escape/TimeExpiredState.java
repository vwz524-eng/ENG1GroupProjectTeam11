package io.github.gegame.escape;

/**
 * Terminal state when the countdown timer expires before escaping.
 */
public class TimeExpiredState extends RunState {
    public TimeExpiredState() {
        super(Type.TIME_EXPIRED, "TIME_EXPIRED");
    }
}

