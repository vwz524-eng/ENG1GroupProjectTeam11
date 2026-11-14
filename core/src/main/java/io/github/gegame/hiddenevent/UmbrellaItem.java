package io.github.gegame.hiddenevent;

import io.github.gegame.Player;
import io.github.gegame.Student;
import io.github.gegame.inventory.FoundItem;

/**
 * Umbrella item unlocked by rubbing the Long Boi statue's beak.
 * Follows the Long Boi Statue Umbrella State Diagram:
 * Hidden -> Available (3 uses) -> Protected (10s) -> Exhausted.
 */
public class UmbrellaItem extends FoundItem {

    public enum UmbrellaState {
        HIDDEN,
        AVAILABLE,
        PROTECTED,
        EXHAUSTED
    }

    public static final int MAX_USES = 3;
    public static final double PROTECTION_DURATION = 10.0;

    private int usesRemaining;
    private UmbrellaState state;

    public UmbrellaItem() {
        super("umbrella", "Umbrella", "Unlocks 10s of goose protection. 3 uses total.", true);
        this.usesRemaining = MAX_USES;
        this.state = UmbrellaState.AVAILABLE;
    }

    @Override
    public void use(Player player) {
        if (!canUse()) {
            return;
        }

        usesRemaining--;
        state = UmbrellaState.PROTECTED;

        if (player instanceof Student) {
            ((Student) player).giveUmbrella();
        }

        if (usesRemaining <= 0) {
            state = UmbrellaState.EXHAUSTED;
            consume();
        }
    }

    public boolean canUse() {
        return usesRemaining > 0 && state != UmbrellaState.EXHAUSTED;
    }

    public int getUsesRemaining() {
        return usesRemaining;
    }

    public void setUsesRemaining(int usesRemaining) {
        this.usesRemaining = Math.max(0, Math.min(MAX_USES, usesRemaining));
        if (this.usesRemaining == 0) {
            this.state = UmbrellaState.EXHAUSTED;
        }
    }

    public UmbrellaState getState() {
        return state;
    }

    public void setState(UmbrellaState state) {
        this.state = state;
    }
}

