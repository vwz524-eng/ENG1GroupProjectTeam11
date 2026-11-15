package io.github.gegame.score;

/**
 * Gets harder based on the swarms:
 * - 2 breadcrumbs, speed (1.0x).
 * - 4 breadcrumbs, speed (2.0x).
 * - 6 breadcrumbs, speed (2.0x).
 */
public record GooseDifficulty(int breadcrumbsRequired, float speedMultiplier) {
    public GooseDifficulty(int breadcrumbsRequired, float speedMultiplier) {
        this.breadcrumbsRequired = Math.max(1, breadcrumbsRequired);
        this.speedMultiplier = speedMultiplier > 0 ? speedMultiplier : 1.0f;
    }

    /**
     * Factory method to create difficulty settings
     *
     * @param swarmNumber 1 WAS for Derwent, 2 for Statue, 3 for Central Hall
     * @return GooseDifficulty configuration
     */
    public static GooseDifficulty forSwarm(int swarmNumber) {
        return switch (swarmNumber) {
            //case 1 -> new GooseDifficulty(2, 1.0f); // this is a duplicate of the default branch and is not needed
            case 2 -> new GooseDifficulty(4, 2.0f);
            case 3 -> new GooseDifficulty(6, 2.0f);
            default -> new GooseDifficulty(2, 1.0f);
        };
    }

}

