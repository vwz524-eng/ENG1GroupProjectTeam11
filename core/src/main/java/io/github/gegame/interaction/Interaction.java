package io.github.gegame.interaction;

import io.github.gegame.Player;

/*
Abstraction base class for player interactions
Regulated by cooldown and produced an InteractionOutcome
 */

public abstract class Interaction {
    private final double range;
    private final Cooldown cooldown;

    public Interaction(double range, Cooldown cooldown) {
        this.range = Math.max(0.0, range);
        this.cooldown = cooldown != null ? cooldown : new Cooldown(1.0);
    }

    /**
    Checks if this interaction can currently be executed
    @return true if ready, false otherwise.
     */

    public boolean canExecute() {
        return cooldown != null && cooldown.isReady();
    }

    /**
    Executes the interaction for the given player
    @param player the player performing the interaction
    @return the outcome produced by the interaction
     */

    public abstract InteractionOutcome execute(Player player);

    public double getRange() {
        return range;
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    protected void setLastOutcome() {
    }
}

