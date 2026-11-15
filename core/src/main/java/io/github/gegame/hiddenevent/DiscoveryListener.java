package io.github.gegame.hiddenevent;

//Listener interface for hidden event discovery and activation lifecycle
//Allows GameSession, GameTracker, and ScoreSystem to record progression

public interface DiscoveryListener {

    //Called when player gets close enough to discover a hidden event

    void onEventDiscovered(HiddenEvent event);

    //Called when player activates and resolves a hidden event

    void onEventActivated(HiddenEvent event, EventOutcome outcome);
}

