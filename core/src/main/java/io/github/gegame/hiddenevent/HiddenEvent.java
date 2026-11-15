package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

//Represents a hidden event in the game world that rewards exploration

public class HiddenEvent {
    private final String eventId;
    private final String eventType;
    private final String interactionPrompt;
    private boolean isDiscovered;
    private boolean isCompleted;

    //Triggers coordinates and detection radius in world units

    private double x;
    private double y;
    private final double triggerRadius;

    private EventOutcome outcome;
    private final EventTrigger trigger;
    private final DiscoveryCondition condition;
    private DiscoveryReward reward;

    public HiddenEvent(String eventId, String eventType, String interactionPrompt, double x, double y, double triggerRadius) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.interactionPrompt = interactionPrompt;
        this.x = x;
        this.y = y;
        this.triggerRadius = triggerRadius > 0 ? triggerRadius : 60.0;
        this.isDiscovered = false;
        this.isCompleted = false;
        this.trigger = new EventTrigger();
        this.condition = new DiscoveryCondition();
        this.reward = new DiscoveryReward();
    }

    //Marks the event as discovered when player discovers hidden area

    public void discover() {
        this.isDiscovered = true;
    }

    /**
    Check if this event can currently be activated by player
    @return true if discovered, not yet completed and conditions met
     */

    public boolean canActivate() {
        return !isCompleted && condition.evaluate() && trigger.check();
    }

    /**
    Activates this hidden event, applying its outcome to player and inventory
    @param player the player activating event
    @param inventory the player's inventory
    @return the outcome produced by this event
     */

    public EventOutcome activate(Player player, Inventory inventory) {
        if (!canActivate()) {
            return null;
        }

        if (outcome != null) {
            outcome.apply(player, inventory);
        }

        if (reward != null && player != null) {
            reward.grant(player);
        }

        resolve();
        return outcome;
    }

    //Activates the event without parameters for basic lifecycle state


    // Resolves event, making it completed so it cannot be triggered repeatedly

    public void resolve() {
        this.isCompleted = true;
        this.isDiscovered = true;
    }

    /**
    Checks if given world coordinates fall within this event's trigger radius.

    @param px the X coordinate to check
    @param py the Y coordinate to check
    @return true if within trigger distance
     */


    public boolean checkTrigger(double px, double py) {
        return distanceTo(px, py) <= triggerRadius;
    }

    //Calculates distance from paint to event's position

    public double distanceTo(double px, double py) {
        double dx = this.x - px;
        double dy = this.y - py;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getInteractionPrompt() {
        return interactionPrompt;
    }

    public boolean isDiscovered() {
        return isDiscovered;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public EventOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(EventOutcome outcome) {
        this.outcome = outcome;
    }

    public void setReward(DiscoveryReward reward) {
        this.reward = reward;
    }
}

