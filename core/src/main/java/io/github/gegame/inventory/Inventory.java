package io.github.gegame.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Manages an inventory of items with a fixed or configurable capacity.
Supports the Observer pattern via InventoryListener so score,
difficulty, and UI subsystems can react to inventory changes
 */

public class Inventory {
    public static final int DEFAULT_CAPACITY = 5;

    private int capacity;
    private final List<Item> items;
    private final List<InventoryListener> listeners;
    private int selectedIndex = 0;

    public Inventory() {
        this(DEFAULT_CAPACITY);
    }

    public Inventory(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.items = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    /**
     * Adds an item to the inventory if space is available
     * @param item the item to add
     * @return true if the item was added, false if inventory is full or item is null
     */

    public boolean addItem(Item item) {
        if (item == null || isFull()) {
            return false;
        }
        boolean added = false;
        // If there are null slots from slot-based assignment, fill first empty slot
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == null) {
                items.set(i, item);
                added = true;
                break;
            }
        }
        if (!added) {
            added = items.add(item);
        }
        if (added) {
            notifyItemAdded(item);
        }
        return added;
    }

    /**
     * Removed an item from the inventory
     * @param item the item to remove
     * @return true if the item was found and removed, false otherwise
     */

    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        boolean removed = items.remove(item);
        if (removed) {
            notifyItemRemoved(item);
        }
        return removed;
    }

    /**
     * Checks if the inventory contins the given item.
     * @param item the item to check
     * @return true if present, false otherwise
     */

    public boolean hasItem(Item item) {
        if (item == null) {
            return false;
        }
        return items.contains(item);
    }

    /**
     * Checks if the inventory contains an item with the given ID
     * @param id the item ID
     * @return true if an item with matching ID exists, false otherwise
     */

    public boolean hasItem(String id) {
        if (id == null) {
            return false;
        }
        for (Item item : items) {
            if (item != null && id.equals(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the inventory has reached its capacity
     * @return true if full, false otherwise
     */

    public boolean isFull() {
        int count = 0;
        for (Item item : items) {
            if (item != null) {
                count++;
            }
        }
        return count >= capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public int getSize() {
        int count = 0;
        for (Item item : items) {
            if (item != null) {
                count++;
            }
        }
        return count;
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Gets the item with the specified ID
     * @param id the item ID
     * @return the item, or null if not found
     */

    public Item getItem(String id) {
        if (id == null) {
            return null;
        }
        for (Item item : items) {
            if (item != null && id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gets the item at the specified index
     * @param index slot index
     * @return the item, or null if empty or out of range
     */

    public Item getItem(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    /**
     * Sets or replaces an item at the specified slot index
     * @param index slot index
     * @return item item to place
     */
    
    public void setItem(int index, Item item) {
        if (index < 0 || index >= capacity) {
            return;
        }
        while (items.size() <= index) {
            items.add(null);
        }
        items.set(index, item);
        if (item != null) {
            notifyItemAdded(item);
        }
    }

    public Item getSelected() {
        return getItem(selectedIndex);
    }

    public void setSelected(int index) {
        setSelectedIndex(index);
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < capacity) {
            this.selectedIndex = index;
            notifyItemSelected(getItem(selectedIndex), selectedIndex);
        }
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void clear() {
        items.clear();
        selectedIndex = 0;
    }

    // ===== Observer Pattern Methods =====

    public void addListener(InventoryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(InventoryListener listener) {
        listeners.remove(listener);
    }

    private void notifyItemAdded(Item item) {
        for (InventoryListener listener : listeners) {
            listener.onItemAdded(item);
        }
    }

    private void notifyItemRemoved(Item item) {
        for (InventoryListener listener : listeners) {
            listener.onItemRemoved(item);
        }
    }

    private void notifyItemSelected(Item item, int index) {
        for (InventoryListener listener : listeners) {
            listener.onItemSelected(item, index);
        }
    }
}

