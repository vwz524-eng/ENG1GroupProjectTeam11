package io.github.gegame.hiddenevent;

import io.github.gegame.Player;

/**
 * Condition determining whether a hidden event can be discovered.
 * Evaluates required items, timing conditions, and discovery probability.
 */
public class DiscoveryCondition {
    private boolean requireItem;
    private boolean requiresTime;
    private float probability;

    public DiscoveryCondition(boolean requireItem, boolean requiresTime, float probability) {
        this.requireItem = requireItem;
        this.requiresTime = requiresTime;
        this.probability = Math.max(0.0f, Math.min(1.0f, probability));
    }

    public DiscoveryCondition() {
        this(false, false, 1.0f);
    }

    public boolean evaluate() {
        if (probability >= 1.0f) {
            return true;
        }
        return Math.random() <= probability;
    }

    public boolean evaluate(Player player, double time) {
        if (requireItem && (player == null || player.getInventory() == null || player.getInventory().isEmpty())) {
            return false;
        }
        if (requiresTime && time <= 0) {
            return false;
        }
        return evaluate();
    }

    public boolean isRequireItem() {
        return requireItem;
    }

    public void setRequireItem(boolean requireItem) {
        this.requireItem = requireItem;
    }

    public boolean isRequiresTime() {
        return requiresTime;
    }

    public void setRequiresTime(boolean requiresTime) {
        this.requiresTime = requiresTime;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = Math.max(0.0f, Math.min(1.0f, probability));
    }
}

