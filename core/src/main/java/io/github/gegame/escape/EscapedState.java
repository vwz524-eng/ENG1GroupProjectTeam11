package io.github.gegame.escape;

/**
 * Terminal state when the player successfully completes the escape objective.
 */
public class EscapedState extends RunState {
    public EscapedState() {
        super(Type.ESCAPED, "ESCAPED");
    }
}

