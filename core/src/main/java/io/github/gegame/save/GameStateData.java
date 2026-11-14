package io.github.gegame.save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object representing the serializable game state.
 * Stores player coordinates, health, score, timer, inventory, and secrets.
 */
public class GameStateData implements Serializable {
    private static final long serialVersionUID = 1L;

    private double playerX;
    private double playerY;
    private int playerHealth;
    private int playerScore;
    private int remainingTime;
    private String difficultyName;
    private List<String> inventoryItemIds = new ArrayList<>();
    private List<String> discoveredEventIds = new ArrayList<>();

    public GameStateData() {
    }

    public GameStateData(double playerX, double playerY, int playerHealth, int playerScore, int remainingTime, String difficultyName) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerHealth = playerHealth;
        this.playerScore = playerScore;
        this.remainingTime = remainingTime;
        this.difficultyName = difficultyName;
    }

    public double getPlayerX() { return playerX; }
    public void setPlayerX(double playerX) { this.playerX = playerX; }

    public double getPlayerY() { return playerY; }
    public void setPlayerY(double playerY) { this.playerY = playerY; }

    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int playerHealth) { this.playerHealth = playerHealth; }

    public int getPlayerScore() { return playerScore; }
    public void setPlayerScore(int playerScore) { this.playerScore = playerScore; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public String getDifficultyName() { return difficultyName; }
    public void setDifficultyName(String difficultyName) { this.difficultyName = difficultyName; }

    public List<String> getInventoryItemIds() { return inventoryItemIds; }
    public void setInventoryItemIds(List<String> inventoryItemIds) { this.inventoryItemIds = inventoryItemIds; }

    public List<String> getDiscoveredEventIds() { return discoveredEventIds; }
    public void setDiscoveredEventIds(List<String> discoveredEventIds) { this.discoveredEventIds = discoveredEventIds; }
}

