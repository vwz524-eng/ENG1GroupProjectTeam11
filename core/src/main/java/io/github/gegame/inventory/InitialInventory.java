package io.github.gegame.inventory;

//Initialised and populates an inventory with starting items.

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

}

