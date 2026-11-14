package io.github.gegame.animation;

//{@link #suffix()} is the exact token used at the end of the filenames in assets.txt
public enum Direction {
    UP("up", 0, 1),      // libGDX's y axis points up?
    DOWN("down", 0, -1),
    LEFT("left", -1, 0),
    RIGHT("right", 1, 0);

    private final String suffix;
    private final int dx;
    private final int dy;

    Direction(String suffix, int dx, int dy) {
        this.suffix = suffix;
        this.dx = dx;
        this.dy = dy;
    }

    public String suffix() { return suffix; }
    public int dx() { return dx; }
    public int dy() { return dy; }
    public boolean isHorizontal() { return this == LEFT || this == RIGHT; }


    //Gets the direction that best matches the movement vector
    // Bias to prevent flickering between two states on a diagonal
    public static Direction fromVector(double dx, double dy, Direction current) {
        if (dx == 0 && dy == 0) return current;

        final double bias = 1.25;
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        boolean horizontal = current.isHorizontal() ? ay <= ax * bias : ax > ay * bias;

        if (horizontal) return dx > 0 ? RIGHT : LEFT;
        return dy > 0 ? UP : DOWN;
    }
}
