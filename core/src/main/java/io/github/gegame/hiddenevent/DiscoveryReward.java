package io.github.gegame.hiddenevent;

import io.github.gegame.Player;

//Reward granted upon discovering a hidden event

public class DiscoveryReward {
    private int scoreBonus;
    private String revealMessage;

    public DiscoveryReward(int scoreBonus, String revealMessage) {
        this.scoreBonus = scoreBonus;
        this.revealMessage = revealMessage;
    }

    public DiscoveryReward() {
        this(100, "Secret Discovered!");
    }

    public void grant() {

    }

    public void grant(Player player) {
        if (player != null && scoreBonus > 0) {
            player.addScore(scoreBonus);
        }
    }

    public int getScoreBonus() {
        return scoreBonus;
    }

    public void setScoreBonus(int scoreBonus) {
        this.scoreBonus = scoreBonus;
    }

    public String getRevealMessage() {
        return revealMessage;
    }

    public void setRevealMessage(String revealMessage) {
        this.revealMessage = revealMessage;
    }
}

