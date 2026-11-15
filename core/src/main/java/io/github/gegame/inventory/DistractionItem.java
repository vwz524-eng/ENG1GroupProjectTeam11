package io.github.gegame.inventory;

import io.github.gegame.Goose;
import io.github.gegame.Player;

//An item that creates a distraction for geese within a given radius for a duration

public class DistractionItem extends Item {
    private final double radius;
    private final double duration;

    public DistractionItem(String id, String name, String description, double radius, double duration) {
        super(id, name, description);
        this.radius = radius;
        this.duration = duration;
    }

    @Override
    public void use(Player player) {
        consume();
    }

    @Override
    public void use(Player player, Goose goose) {
        if (goose != null) {
            double targetX = player != null ? player.getX() + player.getFacingX() * 50.0 : goose.getX();
            double targetY = player != null ? player.getY() + player.getFacingY() * 50.0 : goose.getY();
            goose.distract(targetX, targetY, duration);
        }
        consume();
    }

    public double getRadius() {
        return radius;
    }

    public double getDuration() {
        return duration;
    }

}

