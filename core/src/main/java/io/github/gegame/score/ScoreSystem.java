package io.github.gegame.score;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages player scoring and difficulty configuration
 * bonuses from time survival, hidden event discoveries, and escapes
 */
public class ScoreSystem {
    private int currentScore;
    private DifficultyLevel difficulty;

    private final TimeBonus timeBonus;
    private final DiscoveryBonus discoveryBonus;
    private final EscapeBonus escapeBonus;
    private final Penalty penalty;

    private final List<ScoreListener> listeners = new ArrayList<>();

    public ScoreSystem(DifficultyLevel difficulty) {
        this.currentScore = 0;
        this.difficulty = difficulty != null ? difficulty : DifficultyLevel.NORMAL;
        this.timeBonus = new TimeBonus();
        this.discoveryBonus = new DiscoveryBonus();
        this.escapeBonus = new EscapeBonus();
        this.penalty = new Penalty();
    }

    /**
     * Adds time-based bonus points calculated from the remaining seconds.
     *
     * @param remainingSeconds seconds left on the countdown timer
     */
    public void addTimeBonus(int remainingSeconds) {
        int basePoints = timeBonus.calculate(remainingSeconds);
        int scaled = Math.round(basePoints * difficulty.getBaseMultiplier());
        addPoints(scaled, "Time Survival Bonus");
    }

    /**
     * Default no-arg time bonus method matching the UML signature.
     */
    public void addTimeBonus() {
        addTimeBonus(60); // Default survival milestone
    }

    /**
     * Adds discovery bonus points for discovering a unique hidden event or item.
     *
     * @param discoveryId unique ID of the discovered secret
     */
    public void addDiscoveryBonus(String discoveryId) {
        int basePoints = discoveryBonus.award(discoveryId);
        if (basePoints > 0) {
            int scaled = Math.round(basePoints * difficulty.getBaseMultiplier());
            addPoints(scaled, "Hidden Event Discovery: " + discoveryId);
        }
    }

    /**
     * Adds escape bonus points upon successfully reaching Central Hall.
     *
     * @param escapeTime seconds remaining upon reaching the objective
     */
    public void addEscapeBonus(int escapeTime) {
        int basePoints = escapeBonus.award(escapeTime);
        int scaled = Math.round(basePoints * difficulty.getBaseMultiplier());
        addPoints(scaled, "Central Hall Escape Bonus");
    }

    /**
     * Applies a penalty, deducting points from the current score.
     *
     * @param reason explanation for the penalty
     */
    public void applyPenalty(String reason) {
        int pointsLost = penalty.apply();
        deductPoints(pointsLost, reason != null ? reason : "Goose Encounter Penalty");
    }

    /**
     * Adds raw points scaled by difficulty multiplier.
     *
     * @param points points to add
     * @param reason reason for score addition
     */
    public void addPoints(int points, String reason) {
        if (points <= 0) return;
        int oldScore = currentScore;
        currentScore += points;
        notifyScoreChanged(oldScore, currentScore, reason);
    }

    /**
     * Deducts points from current score without dropping below zero.
     *
     * @param points points to subtract
     * @param reason reason for penalty
     */
    public void deductPoints(int points, String reason) {
        if (points <= 0) return;
        int oldScore = currentScore;
        currentScore = Math.max(0, currentScore - points);
        notifyScoreChanged(oldScore, currentScore, reason);
    }

    /**
     * Gets the player's current encapsulated score.
     *
     * @return current score
     */
    public int getScore() {
        return currentScore;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        if (difficulty != null) {
            this.difficulty = difficulty;
            notifyDifficultyChanged(difficulty);
        }
    }

    public TimeBonus getTimeBonus() {
        return timeBonus;
    }

    public DiscoveryBonus getDiscoveryBonus() {
        return discoveryBonus;
    }

    public EscapeBonus getEscapeBonus() {
        return escapeBonus;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void addListener(ScoreListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    private void notifyScoreChanged(int oldScore, int newScore, String reason) {
        for (ScoreListener l : listeners) {
            l.onScoreChanged(oldScore, newScore, reason);
        }
    }

    private void notifyDifficultyChanged(DifficultyLevel newDiff) {
        for (ScoreListener l : listeners) {
            l.onDifficultyChanged(newDiff);
        }
    }

    /**
     * Resets score and awarded discoveries back to clean initial state.
     */
    public void reset() {
        int old = currentScore;
        currentScore = 0;
        discoveryBonus.reset();
        notifyScoreChanged(old, 0, "Game Reset");
    }
}

