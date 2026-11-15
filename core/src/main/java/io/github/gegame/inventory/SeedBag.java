
package io.github.gegame.inventory;

/**
 * Unlimited seed bag used to distract nearby geese.
 * Has a 5-second cooldown between successful uses.
 */
public class SeedBag extends DistractionItem {

    public SeedBag() {
        super(
            "seed_bag",
            "Seed Bag",
            "Unlimited birdseed (5-second cooldown)",
            150.0,
            5.0
        );
    }

    @Override
    public boolean isUsedUp() {
        return false;
    }

    @Override
    public void consume() {
        // Unlimited uses: do not reduce quantity.
    }
}
