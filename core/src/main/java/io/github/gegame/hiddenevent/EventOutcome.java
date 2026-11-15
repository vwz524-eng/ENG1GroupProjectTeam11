package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;
import io.github.gegame.inventory.Item;

//Represents the outcome of activating HiddenEvent.

public class EventOutcome {
    private final String
        message;
    private final int scoreBonus;
    private Item revealedItem;
    private final int
        healthRestored;

    public EventOutcome(String message, int scoreBonus) {
        this.message = message;
        this.scoreBonus = scoreBonus;
        this.revealedItem = null;
        this.healthRestored = 0;
    }

    //Applies this outcome's effects to the player and inventory

    public void apply(Player player, Inventory inventory) {
        if (revealedItem != null && inventory != null) {
            inventory.addItem(revealedItem);
        }
        if (healthRestored > 0 && player != null) {
            player.heal(healthRestored);
        }
        if (scoreBonus > 0 && player != null) {
            player.addScore(scoreBonus);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setRevealedItem(Item revealedItem) {
        this.revealedItem = revealedItem;
    }

}

