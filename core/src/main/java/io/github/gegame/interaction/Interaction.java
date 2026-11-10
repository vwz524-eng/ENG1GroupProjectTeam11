package io.github.gegame.interaction;

import io.github.gegame.Player;

/**
 * Abstract base class for player interactions.
 * Regulated by a Cooldown and produces an InteractionOutcome.
 */
public abstract class Interaction {
    private double range;
    private Cooldown cooldown;
    private InteractionOutcome lastOutcome;

    public Interaction() {
        this(100.0, new Cooldown(1.0));
    }

    public Interaction(double range, Cooldown cooldown) {
        this.range = Math.max(0.0, range);
        this.cooldown = cooldown != null ? cooldown : new Cooldown(1.0);
    }

    /**
     * Checks if this interaction can currently be executed.
     *
     * @return true if ready (cooldown finished), false otherwise
     */
    public boolean canExecute() {
        return cooldown != null && cooldown.isReady();
    }

    /**
     * Executes the interaction for the given player.
     *
     * @param player the player performing the interaction
     * @return the outcome produced by the interaction
     */
    public abstract InteractionOutcome execute(Player player);

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = Math.max(0.0, range);
    }

    public Cooldown getCooldown() {
        return cooldown;
    }

    public void setCooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    public InteractionOutcome getLastOutcome() {
        return lastOutcome;
    }

    protected void setLastOutcome(InteractionOutcome lastOutcome) {
        this.lastOutcome = lastOutcome;
    }
}

