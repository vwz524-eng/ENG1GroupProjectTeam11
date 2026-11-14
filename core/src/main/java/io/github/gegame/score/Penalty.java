package io.github.gegame.score;

/**
 * Deducts points upon penalties such as goose collisions or expired objectives.
 */
public class Penalty {
    private int pointsLost;

    public Penalty(int pointsLost) {
        this.pointsLost = Math.max(0, pointsLost);
    }

    public Penalty() {
        this(20);
    }

    /**
     * Calculates the points lost for a given penalty reason.
     *
     * @param reason description of why penalty is applied
     * @return points to deduct
     */
    public int apply(String reason) {
        return pointsLost;
    }

    public int getPointsLost() {
        return pointsLost;
    }

    public void setPointsLost(int pointsLost) {
        this.pointsLost = Math.max(0, pointsLost);
    }
}

