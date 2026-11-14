package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.FoundItem;

/**
 * Allergy medication discovered by investigating the tree outside Central Hall.
 * Follows the Tree Allergy Medication State Diagram:
 * Hidden -> Discovered -> Collected -> HealthRestored.
 * Doubles remaining player health without exceeding original maximum (20).
 */
public class AllergyMedicationItem extends FoundItem {

    public enum MedicationState {
        HIDDEN,
        DISCOVERED,
        COLLECTED,
        HEALTH_RESTORED
    }

    public static final int CAP_HEALTH = 20;

    private MedicationState state;

    public AllergyMedicationItem() {
        super("allergy_medication", "Allergy Medication", "Doubles current health up to maximum (20). Single use.", true);
        this.state = MedicationState.COLLECTED;
    }

    @Override
    public void use(Player player) {
        if (state == MedicationState.HEALTH_RESTORED || isUsedUp() || player == null) {
            return;
        }

        int currentHealth = player.getHealth();
        // Doubles remaining health points without exceeding the cap
        int cap = Math.min(CAP_HEALTH, player.getMaxHealth());
        int doubledHealth = Math.min(cap, currentHealth * 2);
        player.setHealth(doubledHealth);

        state = MedicationState.HEALTH_RESTORED;
        consume();
    }

    public MedicationState getState() {
        return state;
    }

    public void setState(MedicationState state) {
        this.state = state;
    }
}

