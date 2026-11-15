package io.github.gegame.hiddenevent;

import io.github.gegame.Player;

//Reward granted upon discovering a hidden event

public class DiscoveryReward {
    private final int scoreBonus;

    public DiscoveryReward(int scoreBonus) {
        this.scoreBonus = scoreBonus;
    }

    public DiscoveryReward() {
        this(100);
    }

    public void grant(Player player) {
        if (player != null && scoreBonus > 0) {
            player.addScore(scoreBonus);
        }
    }

}

