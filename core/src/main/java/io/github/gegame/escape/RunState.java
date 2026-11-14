package io.github.gegame.escape;

import java.util.Objects;

/**
 * Represents the current running state of an escape run.
 * Provides standard state instances and can be specialized by terminal state classes.
 */
public class RunState {

    public enum Type {
        RUNNING,
        PAUSED,
        ESCAPED,
        CAUGHT,
        TIME_EXPIRED
    }

    public static final RunState RUNNING = new RunState(Type.RUNNING, "RUNNING");
    public static final RunState PAUSED = new RunState(Type.PAUSED, "PAUSED");
    public static final RunState ESCAPED = new EscapedState();
    public static final RunState CAUGHT = new CaughtState();
    public static final RunState TIME_EXPIRED = new TimeExpiredState();

    private final Type type;
    private final String name;

    public RunState(Type type, String name) {
        this.type = type;
        this.name = name != null ? name : type.name();
    }

    public Type getType() {
        return type;
    }

    public String name() {
        return name;
    }

    public boolean isTerminal() {
        return type == Type.ESCAPED || type == Type.CAUGHT || type == Type.TIME_EXPIRED;
    }

    public boolean isRunning() {
        return type == Type.RUNNING;
    }

    public boolean isPaused() {
        return type == Type.PAUSED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RunState)) return false;
        RunState other = (RunState) o;
        return type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return name;
    }
}

