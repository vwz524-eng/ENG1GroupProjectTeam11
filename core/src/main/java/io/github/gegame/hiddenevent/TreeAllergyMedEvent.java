package io.github.gegame.hiddenevent;

/**
 * Hidden event for the tree outside Central Hall.
 * Interacting with the tree reveals allergy medication that doubles the player's
 * remaining health points, without exceeding the original maximum of 20.
 */
public class TreeAllergyMedEvent extends HiddenEvent {

    public TreeAllergyMedEvent(double x, double y) {
        super("tree_allergy_med", "TREE", "Investigate tree", x, y, 65.0);
        setOutcome(new TreeEventOutcome());
        setReward(new DiscoveryReward(150, "You found medication under the tree!"));
    }

    public TreeAllergyMedEvent() {
        this(480, 150);
    }
}

