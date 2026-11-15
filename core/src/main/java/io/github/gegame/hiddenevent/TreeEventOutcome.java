package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

/**
 * Outcome triggered by interacting with the tree outside Central Hall.
 * Reveals a one-use medication that doubles remaining allergy resistance.
 */
public class TreeEventOutcome extends EventOutcome {

    public TreeEventOutcome() {
        super(
            "Reveals medication (doubles remaining resistance)",
            "Medication found! Press Space to double remaining resistance (max 20).",
            150
        );
        setRevealedItem(new AllergyMedicationItem());
    }

    @Override
    public void apply(Player player, Inventory inventory) {
        super.apply(player, inventory);
    }
}

