package io.github.gegame.escape;

/**
 * Terminal state when the player is caught by geese or defeated.
 */
public class CaughtState extends RunState {
    public CaughtState() {
        super(Type.CAUGHT, "CAUGHT");
    }
}

