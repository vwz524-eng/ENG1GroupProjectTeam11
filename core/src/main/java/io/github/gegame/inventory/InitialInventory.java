package io.github.gegame.inventory;

/**
 * Initializes and populates an inventory with starting items.
 */
public class InitialInventory {

    /**
     * Populates the given inventory with default starting items.
     *
     * @param inventory the inventory to populate
     */
    public void populate(Inventory inventory) {
        if (inventory == null) {
            return;
        }
        inventory.setItem(0, new SeedBag());
        inventory.setItem(1, new Bread());
    }

    /**
     * Static convenience helper to populate an inventory.
     *
     * @param inventory the inventory to populate
     */
    public static void populateInventory(Inventory inventory) {
        new InitialInventory().populate(inventory);
    }
}

