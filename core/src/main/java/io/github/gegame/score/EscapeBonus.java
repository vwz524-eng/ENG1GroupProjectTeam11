package io.github.gegame.score;

/**
 * Awards bonus points when the student successfully reaches Central Hall
 * before the 5-minute countdown timer expires.
 */
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
     * @param escapeTime remaining seconds upon escape
     * @return total points awarded
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

