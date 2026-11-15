package io.github.gegame.interaction;

import io.github.gegame.Player;

/*
Feeding action performed by player
Consumes a FeedItem and produces an InteractionOutcome with points
 */

public class FeedingAction extends Interaction {
    private final int feedAmount;
    private final FeedItem feedItem;

    public FeedingAction(int feedAmount, FeedItem feedItem) {
        this(feedAmount, feedItem, new Cooldown(1.5));
    }

    public FeedingAction(io.github.gegame.inventory.FeedItem inventoryFeedItem) {
        this(inventoryFeedItem != null ? inventoryFeedItem.getFeedAmount() : 10,
             new FeedItem(inventoryFeedItem != null ? inventoryFeedItem.getName() : "Feed Item",
                          inventoryFeedItem != null ? inventoryFeedItem.getFeedAmount() : 10));
    }

    public FeedingAction(int feedAmount, FeedItem feedItem, Cooldown cooldown) {
        super(120.0, cooldown);
        this.feedAmount = feedAmount;
        this.feedItem = feedItem;
    }

    @Override
    public InteractionOutcome execute(Player player) {
        if (!canExecute()) {
            InteractionOutcome outcome = InteractionOutcome.failure("Feeding action is on cooldown");
            setLastOutcome();
            return outcome;
        }

        if (feedItem == null || !feedItem.consume()) {
            InteractionOutcome outcome = InteractionOutcome.failure("No food available to consume");
            setLastOutcome();
            return outcome;
        }

        getCooldown().start();

        int points = feedAmount * 5;
        if (player != null) {
            player.addScore(points);
        }

        InteractionOutcome outcome = InteractionOutcome.success(
            "Successfully fed with " + feedItem.getName() + " (amount: " + feedAmount + ")",
            points
        );
        setLastOutcome();
        return outcome;
    }

    public int getFeedAmount() {
        return feedAmount;
    }

    public FeedItem getFeedItem() {
        return feedItem;
    }

}

