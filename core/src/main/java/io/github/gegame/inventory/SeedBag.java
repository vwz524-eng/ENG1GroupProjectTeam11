

package io.github.gegame.inventory;

public class SeedBag extends DistractionItem {

    public SeedBag() {
        super(
            "seed_bag",
            "Seed Bag",
            "Unlimited birdseed - no cooldown",
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
        // Unlimited uses: quantity never decreases.
    }
}

