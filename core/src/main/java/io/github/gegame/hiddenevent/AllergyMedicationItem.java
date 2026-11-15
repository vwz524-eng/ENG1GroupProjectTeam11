package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.FoundItem;

/**
 * Allergy medication discovered by investigating the tree outside Central Hall.
 * Follows the Tree Allergy Medication State Diagram:
 * Hidden -> Discovered -> Collected -> HealthRestored.
 * Doubles remaining allergy resistance, capped at the maximum of 20.
 */
public class AllergyMedicationItem extends FoundItem {

    public enum MedicationState {
        HIDDEN,
        DISCOVERED,
        COLLECTED,
        HEALTH_RESTORED
    }


    private MedicationState state;

    public AllergyMedicationItem() {
        super("allergy_medication", "Medication", "Doubles remaining allergy resistance (max 20). One use.", true);
        this.state = MedicationState.COLLECTED;
    }

    @Override
    public void use(Player player) {
        if (state == MedicationState.HEALTH_RESTORED || isUsedUp() || player == null) {
            return;
        }

        int current = player.getHealth();
        int maximum = player.getMaxHealth();
        player.setHealth(Math.min(maximum, current * 2));

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

