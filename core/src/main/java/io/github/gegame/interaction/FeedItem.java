package io.github.gegame.interaction;

/**
 * Represents a consumable feed item used in the interaction system.
 */
public class FeedItem {
    private String name;
    private int nutritionValue;
    private int quantity = 1;

    public FeedItem() {
        this("Bread", 10);
    }

    public FeedItem(String name, int nutritionValue) {
        this.name = name != null ? name : "Feed Item";
        this.nutritionValue = nutritionValue;
        this.quantity = 1;
    }

    public FeedItem(String name, int nutritionValue, int quantity) {
        this.name = name != null ? name : "Feed Item";
        this.nutritionValue = nutritionValue;
        this.quantity = Math.max(0, quantity);
    }

    /**
     * Consumes one unit of this feed item.
     *
     * @return true if successfully consumed, false if no quantity remaining
     */
    public boolean consume() {
        if (quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNutritionValue() {
        return nutritionValue;
    }

    public void setNutritionValue(int nutritionValue) {
        this.nutritionValue = nutritionValue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }
}

