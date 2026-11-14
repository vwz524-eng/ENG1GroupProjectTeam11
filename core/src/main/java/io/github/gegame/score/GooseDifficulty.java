package io.github.gegame.score;

/**
 * Encapsulates goose threat progression across swarms:
 * Swarm 1 (Derwent): 2 breadcrumbs, normal speed (1.0x).
 * Swarm 2 (Long Boi Statue): 4 breadcrumbs, double speed (2.0x).
 * Swarm 3 (Central Hall Tree): 6 breadcrumbs, double speed (2.0x).
 */
public class GooseDifficulty {
    private int breadcrumbsRequired;
    private float speedMultiplier;

    public GooseDifficulty(int breadcrumbsRequired, float speedMultiplier) {
        this.breadcrumbsRequired = Math.max(1, breadcrumbsRequired);
        this.speedMultiplier = speedMultiplier > 0 ? speedMultiplier : 1.0f;
    }

    public GooseDifficulty() {
        this(2, 1.0f);
    }

    /**
     * Factory method creating difficulty settings for a specific swarm tier (1, 2, or 3).
     *
     * @param swarmNumber 1 for Derwent, 2 for Statue, 3 for Central Hall
     * @return GooseDifficulty configuration
     */
    public static GooseDifficulty forSwarm(int swarmNumber) {
        switch (swarmNumber) {
            case 1:
                return new GooseDifficulty(2, 1.0f);
            case 2:
                return new GooseDifficulty(4, 2.0f);
            case 3:
                return new GooseDifficulty(6, 2.0f);
            default:
                return new GooseDifficulty(2, 1.0f);
        }
    }

    public int getBreadcrumbsRequired() {
        return breadcrumbsRequired;
    }

    public void setBreadcrumbsRequired(int breadcrumbsRequired) {
        this.breadcrumbsRequired = Math.max(1, breadcrumbsRequired);
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier > 0 ? speedMultiplier : 1.0f;
    }
}

