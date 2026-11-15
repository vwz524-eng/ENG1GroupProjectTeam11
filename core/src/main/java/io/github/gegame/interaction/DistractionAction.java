package io.github.gegame.interaction;

import io.github.gegame.Player;

/*
Distraction action performed by a player
Triggers a distraction and produces an InteractionOutcome with points
 */

public class DistractionAction extends Interaction {
    private double distractionPower;
    private Distraction distraction;

    public DistractionAction() {
        this(50.0, new Distraction(150.0), new Cooldown(2.0));
    }

    public DistractionAction(double distractionPower, Distraction distraction) {
        this(distractionPower, distraction, new Cooldown(2.0));
    }

    public DistractionAction(io.github.gegame.inventory.DistractionItem inventoryDistractionItem) {
        this(inventoryDistractionItem != null ? inventoryDistractionItem.getRadius() / 3.0 : 50.0,
             new Distraction(inventoryDistractionItem != null ? inventoryDistractionItem.getRadius() : 150.0));
    }

    public DistractionAction(double distractionPower, Distraction distraction, Cooldown cooldown) {
        super(150.0, cooldown);
        this.distractionPower = distractionPower;
        this.distraction = distraction != null ? distraction : new Distraction(150.0);
    }

    @Override
    public InteractionOutcome execute(Player player) {
        if (!canExecute()) {
            InteractionOutcome outcome = InteractionOutcome.failure("Distraction action is on cooldown");
            setLastOutcome(outcome);
            return outcome;
        }

        getCooldown().start();

        if (distraction != null) {
            distraction.trigger();
        }

        int points = (int) (distractionPower * 2);
        if (player != null) {
            player.addScore(points);
        }

        InteractionOutcome outcome = InteractionOutcome.success(
            "Distraction successfully triggered with power " + distractionPower,
            points
        );
        setLastOutcome(outcome);
        return outcome;
    }

    public double getDistractionPower() {
        return distractionPower;
    }

    public void setDistractionPower(double distractionPower) {
        this.distractionPower = distractionPower;
    }

    public Distraction getDistraction() {
        return distraction;
    }

    public void setDistraction(Distraction distraction) {
        this.distraction = distraction;
    }
}

