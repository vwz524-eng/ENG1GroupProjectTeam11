package io.github.gegame.score;

public interface ScoreListener {

    /**
     * Listens for the player's score changing
     *
     * @param oldScore previous score
     * @param newScore new score
     * @param reason why the score has changed
     */
    void onScoreChanged(int oldScore, int newScore, String reason);

    /**
     * Called when difficulty level changes
     *
     * @param newDifficulty new difficulty setting
     */
    void onDifficultyChanged(DifficultyLevel newDifficulty);
}

