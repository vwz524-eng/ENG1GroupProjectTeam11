package io.github.gegame.inventory;

/**
 * Seed bag distraction item, thrown to distract geese.
 */
public class SeedBag extends DistractionItem {
    public SeedBag() {
        super("seed_bag", "Seed Bag", "Throws seeds to distract nearby geese", 150.0, 5.0);
    }
}

