package io.github.gegame.score;

public class Penalty {
    private final int pointsLost;

    public Penalty(int pointsLost) {
        this.pointsLost = Math.max(0, pointsLost);
    }

    public Penalty() {
        this(20);
    }

    /**
     * Calculates the points lost
     *
     * @return points to deduct
     */
    public int apply() {
        return pointsLost;
    }

}

