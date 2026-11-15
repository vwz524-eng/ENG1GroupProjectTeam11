package io.github.gegame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.gegame.interaction.Interaction;
import io.github.gegame.interaction.InteractionOutcome;
import io.github.gegame.inventory.Inventory;

/**
 * Represents the playable character or student in the game
 * Holds the name, score, health, position, facing direction, inventory,
 * also handles the interactions.
 */
public class Player extends Sprite {
    private String name = "Player";
    private int score = 0;
    private final List<Interaction> interactions = new ArrayList<>();

    protected static final int DEFAULT_MAX_HEALTH = 100;
    protected int maxHealth = DEFAULT_MAX_HEALTH;
    protected int health = DEFAULT_MAX_HEALTH;

    protected double facingX = 1;
    protected double facingY = 0;

    protected Inventory inventory;

    public Player(double x, double y, int width, int height) {
        super(x, y, width, height);
        this.inventory = new Inventory();
    }

    public Player(double x, double y) {
        this(x, y, 32, 32);
    }

    public Player(String name, double x, double y) {
        this(x, y, 32, 32);
        this.name = name != null ? name : "Player";
    }

    /**
     * Initiates and executes an interaction for the student
     *
     * @param interaction the interaction to happen
     * @return the outcome of the interaction
     */
    public InteractionOutcome interact(Interaction interaction) {
        if (interaction == null)
        {
            return InteractionOutcome.failure("Interaction cannot be null");
        }
        if (!interactions.contains(interaction))
        {
            interactions.add(interaction);
        }
        return interaction.execute(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "Player";
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = Math.max(0, score);
    }

    public void addScore(int points) {
        if (points > 0) {
            this.score += points;
        }
    }

    public List<Interaction> getInteractions() {
        return Collections.unmodifiableList(interactions);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void heal(int amount) {
        if (amount > 0) {
            this.health = Math.min(maxHealth, health + amount);
        }
    }

    /* restores player hp, pretty self explainatory  */
    public void restoreHealth() {
        this.health = this.maxHealth;
    }

    /** Restores a specified amount of health */
    public void restoreHealth(int amount) {
        heal(amount);
    }

    /** responsible for murdering the player in a totally non-violent way -
     * it's allergies I swear, ignore the fact that we call it death like 193 times in the src code
     */
    public boolean isDead() {
        return health <= 0;
    }

    // checks hp is not < 0 and setts player health value to hp-dmg taken
    public void takeDamage(int amount) {
        if (!isDead()) {
            this.health = Math.max(0, health - amount);
        }
    }

    public double getFacingX() {
        return facingX;
    }

    public double getFacingY() {
        return facingY;
    }

}
