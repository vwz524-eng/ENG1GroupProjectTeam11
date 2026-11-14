package io.github.gegame;

import io.github.gegame.escape.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimedEscapeTest {

    private CountdownTimer timer;
    private EscapeObjective objective;

    @BeforeEach
    public void setUp() {
        timer = new CountdownTimer(300);
        objective = new EscapeObjective(true);
    }

    @Test
    public void testTimerInitialization() {
        assertEquals(300, timer.getDuration());
        assertEquals(300, timer.getRemaining());
        assertEquals(WarningLevel.NORMAL, timer.getWarningLevel());
        assertTrue(timer.getRunState().isPaused());
        assertEquals("05:00", timer.getFormattedTime());
    }

    @Test
    public void testTimerStartAndTick() {
        timer.start();
        assertTrue(timer.getRunState().isRunning());

        timer.tick();
        assertEquals(299, timer.getRemaining());
        assertEquals("04:59", timer.getFormattedTime());
    }

    @Test
    public void testTimerPause() {
        timer.start();
        timer.tick();
        assertEquals(299, timer.getRemaining());

        timer.pause();
        assertTrue(timer.getRunState().isPaused());
        timer.tick();
        // Should not change when paused
        assertEquals(299, timer.getRemaining());
    }

    @Test
    public void testWarningLevelProgression() {
        timer.setRemaining(150);
        assertEquals(WarningLevel.NORMAL, timer.getWarningLevel());

        timer.setRemaining(120);
        assertEquals(WarningLevel.WARNING, timer.getWarningLevel());

        timer.setRemaining(50);
        assertEquals(WarningLevel.WARNING, timer.getWarningLevel());

        timer.setRemaining(30);
        assertEquals(WarningLevel.CRITICAL, timer.getWarningLevel());

        timer.setRemaining(5);
        assertEquals(WarningLevel.CRITICAL, timer.getWarningLevel());
    }

    @Test
    public void testTimerExpiration() {
        timer.start();
        timer.expire();

        assertTrue(timer.isExpired());
        assertEquals(0, timer.getRemaining());
        assertEquals(WarningLevel.CRITICAL, timer.getWarningLevel());
        assertTrue(timer.getRunState() instanceof TimeExpiredState || timer.getRunState().equals(RunState.TIME_EXPIRED));
        assertTrue(timer.getRunState().isTerminal());
    }

    @Test
    public void testEscapeObjectiveSatisfied() {
        assertFalse(objective.isCompleted());
        assertFalse(objective.isSatisfied());

        objective.complete();
        assertTrue(objective.isCompleted());
        assertTrue(objective.isSatisfied());

        // Optional objective
        EscapeObjective optional = new EscapeObjective(false);
        assertTrue(optional.isSatisfied());
    }

    @Test
    public void testObjectiveEvaluatesTimer() {
        assertTrue(objective.canBeCompleted(timer));

        timer.expire();
        assertFalse(objective.canBeCompleted(timer));
    }

    @Test
    public void testRunStateSubclasses() {
        RunState escaped = new EscapedState();
        assertTrue(escaped instanceof RunState);
        assertEquals(RunState.Type.ESCAPED, escaped.getType());
        assertTrue(escaped.isTerminal());

        RunState caught = new CaughtState();
        assertTrue(caught instanceof RunState);
        assertEquals(RunState.Type.CAUGHT, caught.getType());
        assertTrue(caught.isTerminal());

        RunState timeExpired = new TimeExpiredState();
        assertTrue(timeExpired instanceof RunState);
        assertEquals(RunState.Type.TIME_EXPIRED, timeExpired.getType());
        assertTrue(timeExpired.isTerminal());

        RunState running = RunState.RUNNING;
        assertFalse(running.isTerminal());
        assertTrue(running.isRunning());
    }

    @Test
    public void testDeltaTick() {
        timer.start();
        timer.tick(0.4f);
        assertEquals(300, timer.getRemaining()); // accumulated 0.4s

        timer.tick(0.7f); // accumulated 1.1s -> 1 tick
        assertEquals(299, timer.getRemaining());
    }
}

