package io.github.gegame.score;

public class Penalty {
    private int pointsLost;

    public Penalty(int pointsLost) {
        this.pointsLost = Math.max(0, pointsLost);
    }

    public Penalty() {
        this(20);
    }

    /**
     * Calculates the points lost
     *
     * @param reason reason for deduction
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

