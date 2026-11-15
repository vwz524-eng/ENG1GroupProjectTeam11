package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

/*
Outcome triggered by interacting with tree outside Central Hall
Reveals a one use medication that doubles remaining allergy resistance.
 */

public class TreeEventOutcome extends EventOutcome {

    public TreeEventOutcome() {
        super(
            "Medication found! Press Space to inrease remaining resistance by 10 (max 20).",
            150
        );
        setRevealedItem(new AllergyMedicationItem());
    }

    @Override
    public void apply(Player player, Inventory inventory) {
        super.apply(player, inventory);
    }
}

