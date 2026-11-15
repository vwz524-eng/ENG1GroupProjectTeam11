package io.github.gegame.score;

public class EscapeBonus {
    private int points;

    public EscapeBonus(int points) {
        this.points = points > 0 ? points : 500;
    }

    public EscapeBonus() {
        this(500);
    }

    /**
     * Awards escape points, optionally combining base bonus with remaining time speed bonus.
     *
     * @param escapeTime remaining time when escaped
     * @return points awarded
     */
    public int award(int escapeTime) {
        int speedBonus = Math.max(0, escapeTime);
        return points + speedBonus;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points > 0 ? points : 500;
    }
}

