package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

/**
 * Outcome triggered by interacting with the tree outside Central Hall.
 * Restores player health by doubling remaining health points (capped at 20).
 */
public class TreeEventOutcome extends EventOutcome {

    public static final int MAX_RESTORE_CAP = 20;

    public TreeEventOutcome() {
        super(
            "Restores player health (Doubles remaining health up to 20)",
            "You investigated the tree and found allergy medication! Health has doubled.",
            150
        );
        setRevealedItem(new AllergyMedicationItem());
    }

    @Override
    public void apply(Player player, Inventory inventory) {
        if (player != null) {
            int currentHealth = player.getHealth();
            int cap = Math.min(MAX_RESTORE_CAP, player.getMaxHealth());
            int doubled = Math.min(cap, currentHealth * 2);
            int restoredAmount = doubled - currentHealth;
            if (restoredAmount > 0) {
                setHealthRestored(restoredAmount);
            }
        }
        super.apply(player, inventory);
    }
}

