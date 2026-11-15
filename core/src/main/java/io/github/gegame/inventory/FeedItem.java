package io.github.gegame.inventory;

import io.github.gegame.Goose;
import io.github.gegame.Player;

//An item that feeds geese to calm or pause them

public class FeedItem extends Item {
    private final int feedAmount;

    public FeedItem(String id, String name, String description, int feedAmount) {
        super(id, name, description);
        this.feedAmount = feedAmount;
    }

    @Override
    public void use(Player player) {
        consume();
    }

    @Override
    public void use(Player player, Goose goose) {
        if (goose != null) {
            goose.feed(feedAmount);
        }
        consume();
    }

    public int getFeedAmount() {
        return feedAmount;
    }

}

