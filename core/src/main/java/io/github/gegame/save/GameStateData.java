package io.github.gegame.save;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object representing a serializable game state
 * Stores the player's coordinates, health, score, timer, inventory, and the secrets
 */
public class GameStateData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final double playerX;
    private final int playerHealth;
    private final int playerScore;
    private final int remainingTime;
    private final String difficultyName;
    private final List<String> inventoryItemIds = new ArrayList<>();
    private final List<String> discoveredEventIds = new ArrayList<>();

    public GameStateData(double playerX, int playerHealth, int playerScore, int remainingTime, String difficultyName) {
        this.playerX = playerX;
        this.playerHealth = playerHealth;
        this.playerScore = playerScore;
        this.remainingTime = remainingTime;
        this.difficultyName = difficultyName;
    }

    public double getPlayerX() { return playerX; }

    public int getPlayerHealth() { return playerHealth; }

    public int getPlayerScore() { return playerScore; }

    public int getRemainingTime() { return remainingTime; }

    public String getDifficultyName() { return difficultyName; }

    public List<String> getInventoryItemIds() { return inventoryItemIds; }

    public List<String> getDiscoveredEventIds() { return discoveredEventIds; }

}

