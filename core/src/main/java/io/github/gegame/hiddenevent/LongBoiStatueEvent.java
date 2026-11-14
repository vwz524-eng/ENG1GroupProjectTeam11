package io.github.gegame.hiddenevent;

/**
 * Hidden event for the Long Boi statue.
 * Rubbing the statue's beak unlocks an umbrella with 3 uses that prevents health loss
 * from goose contact for 10 seconds per activation.
 */
public class LongBoiStatueEvent extends HiddenEvent {

    public LongBoiStatueEvent(double x, double y) {
        super("statue_umbrella", "STATUE", "Rub Long Boi's beak", x, y, 65.0);
        setOutcome(new StatueEventOutcome());
        setReward(new DiscoveryReward(150, "You rubbed Long Boi's beak! Umbrella unlocked!"));
    }

    public LongBoiStatueEvent() {
        this(300, 300);
    }
}

