package io.github.gegame.hiddenevent;

/**
 * Listener interface for hidden event discovery and activation lifecycle.
 * Allows GameSession, GameTracker, and ScoreSystem to record progression.
 */
public interface DiscoveryListener {

    /**
     * Called when a player gets close enough to discover a hidden event.
     *
     * @param event the discovered event
     */
    void onEventDiscovered(HiddenEvent event);

    /**
     * Called when a player activates and resolves a hidden event.
     *
     * @param event   the activated event
     * @param outcome the resulting outcome
     */
    void onEventActivated(HiddenEvent event, EventOutcome outcome);
}

