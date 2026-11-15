package io.github.gegame.inventory;

import io.github.gegame.Player;
import io.github.gegame.Sprite;

/*
In game pickup player representing an item places in the world
Can be picked up by a player on contact and added to their inventory
 */

public class ItemPickup extends Sprite {
    private Item item;
    private boolean collected = false;

    public ItemPickup(Item item, double x, double y) {
        this(item, x, y, 24, 24);
    }

    public ItemPickup(Item item, double x, double y, int width, int height) {
        super(x, y, width, height);
        this.item = item;
    }

    /**
     * Called when a player makes contact with this pickup
     * @param player the player touching pickup
     */

    public void onPlayerContact(Player player) {
        if (player == null || collected) {
            return;
        }
        if (pickUp(player.getInventory())) {
            this.collected = true;
            if (item instanceof FoundItem) {
                ((FoundItem) item).setCollected(true);
            }
        }
    }

    /**
     * Attempts to add the referenced item to the given inventory
     * @param inventory the inventory to add the item into
     * @return true if added, false if inventory is null or full or already collected
     */

    public boolean pickUp(Inventory inventory) {
        if (inventory == null || item == null || collected) {
            return false;
        }
        if (inventory.isFull()) {
            return false;
        }
        return inventory.addItem(item);
    }

    /**
     * Checks collision with a player and triggers onPlayerContact if colliding
     * @param player the player to check
     */

    public void update(Player player) {
        if (!collected && player != null && collidesWith(player)) {
            onPlayerContact(player);
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isCollected() {
        return collected;
    }

}

