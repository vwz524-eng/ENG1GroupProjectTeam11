package io.github.gegame.score;

/**
 * Observer listener interface for score and difficulty changes.
 */
public interface ScoreListener {

    /**
     * Called whenever the player's score changes.
     *
     * @param oldScore previous score
     * @param newScore updated score
     * @param reason   description or category of change
     */
    void onScoreChanged(int oldScore, int newScore, String reason);

    /**
     * Called when difficulty level changes.
     *
     * @param newDifficulty the new difficulty setting
     */
    void onDifficultyChanged(DifficultyLevel newDifficulty);
}

