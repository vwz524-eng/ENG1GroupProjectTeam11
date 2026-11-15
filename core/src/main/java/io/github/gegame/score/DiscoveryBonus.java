package io.github.gegame.score;

import java.util.HashSet;
import java.util.Set;

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
     * Responible for bonus points upon discovery of a hidden event
     *
     * @param discoveryId unique ID
     * @return points awarded, 0 as a fallback
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

    public void reset() {
        awardedDiscoveries.clear();
    }
}

