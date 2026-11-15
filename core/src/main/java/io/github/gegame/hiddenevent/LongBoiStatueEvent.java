package io.github.gegame.hiddenevent;

/*
Hidden event for the LongBoi statue
Interacting with statue unlocks umbrella with 3 uses that prevents allergy loss
from goose contact for 10 seconds per activation
 */

public class LongBoiStatueEvent extends HiddenEvent {

    public LongBoiStatueEvent(double x, double y) {
        super("statue_umbrella", "STATUE", "Rub Long Boi's beak", x, y, 65.0);
        setOutcome(new StatueEventOutcome());
        setReward(new DiscoveryReward(150));
    }
}

