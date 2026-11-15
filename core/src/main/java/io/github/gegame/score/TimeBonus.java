package io.github.gegame.score;

/**
 * Gives points the longer the player survives in the game, up to the 5 minutes
 * or based on remaining time upon successful escape
 */
public class TimeBonus {
    private final int pointsPerSecond;
    private int points;

    public TimeBonus(int pointsPerSecond) {
        this.pointsPerSecond = Math.max(1, pointsPerSecond);
        this.points = 0;
    }

    public TimeBonus() {
        this(2);
    }

    /**
     * Calculates the additional time bonus
     *
     * @param remainingTime time (s)
     * @return number of points awared to score
     */
    public int calculate(int remainingTime) {
        if (remainingTime <= 0) {
            this.points = 0;
            return 0;
        }
        this.points = remainingTime * pointsPerSecond;
        return this.points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

