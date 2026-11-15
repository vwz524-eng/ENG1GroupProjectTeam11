package io.github.gegame.inventory;

import io.github.gegame.Goose;
import io.github.gegame.Player;
import java.util.Objects;

//Abstract base class for all items in the inventory system

public abstract class Item {
    private String id;
    private String name;
    private final String description;
    private int quantity = 1;

    public Item() {
        this("", "", "");
    }

    public Item(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Item(String id, String name, String description, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    /**
     * Abstract method to use the item with the given player
     * @param player the player using the item
     */

    public abstract void use(Player player);

    /**
     * Overloaded method to use the item with player targeting goose
     * @param player player using the item
     * @param goose goose being targeted
     */

    public void use(Player player, Goose goose) {
        use(player);
    }

    /**
     * Checks if this item has been used up
     * @return true if quantity less than 0, false otherwise
     */

    public boolean isUsedUp() {
        return quantity <= 0;
    }

    public void consume() {
        if (quantity > 0) {
            quantity--;
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}

