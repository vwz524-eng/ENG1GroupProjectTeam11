package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

/**
 * Outcome triggered by interacting with the Long Boi statue.
 * Reveals an umbrella (with 3 uses) and adds it to the player's inventory.
 */
public class StatueEventOutcome extends EventOutcome {

    public StatueEventOutcome() {
        super(
            "Reveals umbrella (Adds umbrella with 3 uses to inventory)",
            "You rubbed Long Boi's beak! An umbrella has been added to your inventory.",
            150
        );
        setRevealedItem(new UmbrellaItem());
    }

    @Override
    public void apply(Player player, Inventory inventory) {
        super.apply(player, inventory);
    }
}

