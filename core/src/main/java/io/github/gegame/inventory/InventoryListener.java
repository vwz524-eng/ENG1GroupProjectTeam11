package io.github.gegame.inventory;

/*
Observer interfaces for listening to inventory events
Can be implemented by ScoreManager, DifficultyManager, QuestManager or UI
 */

public interface InventoryListener {

    /**
     * Called when an item is added to the inventory
     * @param item the item that was added
     */

    void onItemAdded(Item item);

    /**
     * Called when an item is removed from the inventory
     * @param item the item was removed
     */

    void onItemRemoved(Item item);

    /**
     * Called when the active item selection change
     * @param item the newly selected item
     * @param index the slot index
     */

    void onItemSelected(Item item, int index);
}

