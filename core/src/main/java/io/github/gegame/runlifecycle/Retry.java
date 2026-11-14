package io.github.gegame.runlifecycle;

/**
 * Abstract class representing retry, restart, and reset lifecycle capabilities
 * as defined in the Game Run Lifecycle class diagram.
 */
public abstract class Retry {
    public abstract void Restart_game();
    public abstract void Restart_Score();
    public abstract void Reset_Level();
}

