package io.github.gegame.interaction;

/*
Protects the game against extreme player activities such as rapid key spamming
or macto bursts that could cause crash conditions or state instability
Fulfills risk assessment R21: "Have a function that deals with extreme game activities such as key spams"
 */

public class ActionSpamFilter {
    private final double minIntervalSeconds;
    private double lastActionTime;

    public ActionSpamFilter(double minIntervalSeconds) {
        this.minIntervalSeconds = Math.max(0.05, minIntervalSeconds);
        this.lastActionTime = -1.0;
    }

    public ActionSpamFilter() {
        this(0.12); // 120ms debounce interval
    }

    /*
    Check whether action is allowed or should be blocked due to spam
    @param currentTime current timestamp or cumulative game time in s
    @return true if permitted, false if throttled as spam
     */

    public boolean allowAction(double currentTime) {
        if (lastActionTime < 0 || (currentTime - lastActionTime) >= minIntervalSeconds) {
            lastActionTime = currentTime;
            return true;
        }
        return false;
    }

    public double getMinIntervalSeconds() {
        return minIntervalSeconds;
    }

    public void reset() {
        this.lastActionTime = -1.0;
    }
}

