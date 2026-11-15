package io.github.gegame.runlifecycle;

/**
 * Abstract class for retry, restart, and reset lifecycle
 */
public abstract class Retry {
    public abstract void Restart_game();
    public abstract void Restart_Score();
    public abstract void Reset_Level();
}

