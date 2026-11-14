package io.github.gegame.score;

/**
 * Calculates time-based bonuses.
 * Gives points the longer the player survives in the game, up to the 5-minute limit,
 * or based on remaining time upon successful escape.
 */
public class TimeBonus {
    private int pointsPerSecond;
    private int points;

    public TimeBonus(int pointsPerSecond) {
        this.pointsPerSecond = Math.max(1, pointsPerSecond);
        this.points = 0;
    }

    public TimeBonus() {
        this(2);
    }

    /**
     * Calculates the time bonus based on remaining seconds.
     *
     * @param remainingTime time in seconds
     * @return points awarded
     */
    public int calculate(int remainingTime) {
        if (remainingTime <= 0) {
            this.points = 0;
            return 0;
        }
        this.points = remainingTime * pointsPerSecond;
        return this.points;
    }

    public int getPointsPerSecond() {
        return pointsPerSecond;
    }

    public void setPointsPerSecond(int pointsPerSecond) {
        this.pointsPerSecond = Math.max(1, pointsPerSecond);
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}

