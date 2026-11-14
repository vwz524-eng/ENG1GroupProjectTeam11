package io.github.gegame.score;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Awards bonus points when the player discovers hidden events and secret locations.
 * Prevents duplicate awards for the same discovery ID.
 */
public class DiscoveryBonus {
    private int points;
    private final Set<String> awardedDiscoveries = new HashSet<>();

    public DiscoveryBonus(int points) {
        this.points = points > 0 ? points : 150;
    }

    public DiscoveryBonus() {
        this(150);
    }

    /**
     * Awards discovery points if this discovery has not already been claimed.
     *
     * @param discoveryId unique ID of the hidden event / secret
     * @return points awarded (0 if already discovered or id is null)
     */
    public int award(String discoveryId) {
        if (discoveryId == null || awardedDiscoveries.contains(discoveryId)) {
            return 0;
        }
        awardedDiscoveries.add(discoveryId);
        return points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points > 0 ? points : 150;
    }

    public Set<String> getAwardedDiscoveries() {
        return Collections.unmodifiableSet(awardedDiscoveries);
    }

    public void reset() {
        awardedDiscoveries.clear();
    }
}

