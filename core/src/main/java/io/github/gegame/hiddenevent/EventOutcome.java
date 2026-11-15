package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;
import io.github.gegame.inventory.Item;

//Represents the outcome of activating HiddenEvent.

public class EventOutcome {
    private String outcomeDescription;
    private String message;
    private int threatChange;
    private int scoreBonus;
    private Item revealedItem;
    private int healthRestored;

    public EventOutcome(String outcomeDescription, String message, int scoreBonus) {
        this.outcomeDescription = outcomeDescription;
        this.message = message;
        this.scoreBonus = scoreBonus;
        this.threatChange = 0;
        this.revealedItem = null;
        this.healthRestored = 0;
    }

    public EventOutcome(String outcomeDescription, String message) {
        this(outcomeDescription, message, 100);
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

    public String getOutcomeDescription() {
        return outcomeDescription;
    }

    public void setOutcomeDescription(String outcomeDescription) {
        this.outcomeDescription = outcomeDescription;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getThreatChange() {
        return threatChange;
    }

    public void setThreatChange(int threatChange) {
        this.threatChange = threatChange;
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public void setScoreBonus(int scoreBonus) {
        this.scoreBonus = scoreBonus;
    }

    public Item getRevealedItem() {
        return revealedItem;
    }

    public void setRevealedItem(Item revealedItem) {
        this.revealedItem = revealedItem;
    }

    public int getHealthRestored() {
        return healthRestored;
    }

    public void setHealthRestored(int healthRestored) {
        this.healthRestored = healthRestored;
    }
}

