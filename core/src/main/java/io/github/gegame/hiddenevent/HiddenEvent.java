package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.inventory.Inventory;

//Represents a hidden event in the game world that rewards exploration

public class HiddenEvent {
    private String eventId;
    private String eventType;
    private String interactionPrompt;
    private boolean isDiscovered;
    private boolean isCompleted;

    //Triggers coordinates and detection radius in world units

    private double x;
    private double y;
    private double triggerRadius;

    private EventOutcome outcome;
    private EventTrigger trigger;
    private DiscoveryCondition condition;
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

    public HiddenEvent(String eventId, String interactionPrompt, double x, double y) {
        this(eventId, "GENERIC", interactionPrompt, x, y, 60.0);
    }

    public HiddenEvent(String eventId, String interactionPrompt) {
        this(eventId, "GENERIC", interactionPrompt, 0, 0, 60.0);
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
        return !isCompleted && (condition == null || condition.evaluate()) && (trigger == null || trigger.check());
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


    public void activate() {
        if (canActivate()) {
            resolve();
        }
    }

    // Resovles event, making it completed so it cannot be triggered repeatedly

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

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getInteractionPrompt() {
        return interactionPrompt;
    }

    public void setInteractionPrompt(String interactionPrompt) {
        this.interactionPrompt = interactionPrompt;
    }

    public boolean isDiscovered() {
        return isDiscovered;
    }

    public void setDiscovered(boolean discovered) {
        isDiscovered = discovered;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
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

    public double getTriggerRadius() {
        return triggerRadius;
    }

    public void setTriggerRadius(double triggerRadius) {
        this.triggerRadius = triggerRadius;
    }

    public EventOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(EventOutcome outcome) {
        this.outcome = outcome;
    }

    public EventTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(EventTrigger trigger) {
        this.trigger = trigger;
    }

    public DiscoveryCondition getCondition() {
        return condition;
    }

    public void setCondition(DiscoveryCondition condition) {
        this.condition = condition;
    }

    public DiscoveryReward getReward() {
        return reward;
    }

    public void setReward(DiscoveryReward reward) {
        this.reward = reward;
    }
}

