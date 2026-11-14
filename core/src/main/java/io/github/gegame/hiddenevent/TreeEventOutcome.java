package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

/**
 * Outcome triggered by interacting with the tree outside Central Hall.
 * Reveals Allergy Medication that heals the player by 50 health points.
 */
public class TreeEventOutcome extends EventOutcome {

    public static final int HEAL_AMOUNT = AllergyMedicationItem.HEAL_AMOUNT;

    public TreeEventOutcome() {
        super(
            "Reveals Medication (Heals 50 health)",
            "You investigated the tree and found Medication! Use it to heal 50 HP.",
            150
        );
        setRevealedItem(new AllergyMedicationItem());
    }

    @Override
    public void apply(Player player, Inventory inventory) {
        super.apply(player, inventory);
    }
}

