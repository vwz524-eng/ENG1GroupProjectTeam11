package io.github.gegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import io.github.gegame.escape.*;
import io.github.gegame.hiddenevent.*;
import io.github.gegame.interaction.ActionSpamFilter;
import io.github.gegame.inventory.*;
import io.github.gegame.score.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game loop demonstrating Student, Goose, ItemPickups, Inventory, and Timed Escape.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Student student;
    private Goose goose;
    private final List<ItemPickup> pickups = new ArrayList<>();
    private InventoryUI inventoryUI;

    // Timed Escape system components
    private CountdownTimer countdownTimer;
    private EscapeObjective escapeObjective;

    // Hidden Event Discovery system
    private HiddenEventManager hiddenEventManager;

    // Score & Difficulty system
    private ScoreSystem scoreSystem;

    // Accessibility & Quality Requirements
    private boolean colorBlindMode = false;

    // Extreme activity / spam protection (Risk Assessment R21)
    private final ActionSpamFilter spamFilter = new ActionSpamFilter(0.12);
    private double gameTime = 0.0;

    private String notificationMessage = "Welcome! WASD to move, 1-5 to select, SPACE to use items";
    private Timer.Task clearNotificationTask;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();

        resetGame();
    }

    private void resetGame() {
        // Free the old entities' textures so restarting (R) doesn't leak GPU memory
        if (student != null) student.dispose();
        if (goose != null) goose.dispose();

        student = new Student(100, 240);
        goose = new Goose(500, 240);
        scoreSystem = new ScoreSystem(DifficultyLevel.NORMAL);
        student.setScore(0);

        // Initialize 5-minute timed escape countdown
        countdownTimer = new CountdownTimer(300);
        countdownTimer.start();
        escapeObjective = new EscapeObjective(true);

        pickups.clear();
        // Populate world with pickups for demonstration
        pickups.add(new ItemPickup(new Bread(), 250, 180));
        pickups.add(new ItemPickup(new SeedBag(), 400, 320));
        pickups.add(new ItemPickup(new FoundItem("umbrella", "Umbrella", "Gives 10s of goose protection"), 300, 380));

        inventoryUI = new InventoryUI(student.getInventory());

        // Initialize Hidden Event Discovery
        hiddenEventManager = new HiddenEventManager();
        hiddenEventManager.registerEvent(new LongBoiStatueEvent(260, 330));
        hiddenEventManager.registerEvent(new TreeAllergyMedEvent(470, 150));

        hiddenEventManager.addListener(new DiscoveryListener() {
            @Override
            public void onEventDiscovered(HiddenEvent event) {
                showNotification("Discovered: " + event.getInteractionPrompt() + " (Press E)");
            }

            @Override
            public void onEventActivated(HiddenEvent event, EventOutcome outcome) {
                if (event != null) {
                    scoreSystem.addDiscoveryBonus(event.getEventId());
                    student.setScore(scoreSystem.getScore());
                }
                if (outcome != null) {
                    showNotification(outcome.getMessage());
                }
            }
        });

        // Demonstrate Observer pattern: Score listens to inventory events
        student.getInventory().addListener(new InventoryListener() {
            @Override
            public void onItemAdded(Item item) {
                if (item instanceof FoundItem) {
                    scoreSystem.addPoints(100, "Rare Item: " + item.getName());
                    student.setScore(scoreSystem.getScore());
                    showNotification("Picked up rare item: " + item.getName() + " (+100 pts)!");
                } else {
                    scoreSystem.addPoints(25, "Resource Item: " + item.getName());
                    student.setScore(scoreSystem.getScore());
                    showNotification("Picked up: " + item.getName() + " (+25 pts)!");
                }
            }

            @Override
            public void onItemRemoved(Item item) {
            }

            @Override
            public void onItemSelected(Item item, int index) {
            }
        });

        showNotification("Escape to Central Hall before the 5-minute timer expires!");
    }

    public void showNotification(String message) {
        this.notificationMessage = message;
        if (clearNotificationTask != null) {
            clearNotificationTask.cancel();
        }
        clearNotificationTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                notificationMessage = null;
            }
        }, 3.5f);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        gameTime += dt;
        handleInput(dt);

        boolean isGameRunning = countdownTimer.getRunState().isRunning() && !student.isDead() && !escapeObjective.isCompleted();
        HiddenEvent nearbyEvent = isGameRunning ? hiddenEventManager.checkProximity(student) : null;

        if (isGameRunning) {
            // Tick escape countdown timer
            countdownTimer.tick(dt);

            // Update entities
            student.update(dt);
            goose.update(dt, student);
            student.checkGooseCollision(goose);

            // Check if player reaches Central Hall (right exit zone)
            if (student.getX() >= 580) {
                escapeObjective.complete();
                countdownTimer.setRunState(RunState.ESCAPED);
                scoreSystem.addEscapeBonus(countdownTimer.getRemaining());
                scoreSystem.addTimeBonus(countdownTimer.getRemaining());
                student.setScore(scoreSystem.getScore());
                showNotification("ESCAPED! Reached Central Hall on time! (+" + scoreSystem.getScore() + " pts)");
            }

            // Check defeat conditions
            if (student.isDead()) {
                countdownTimer.setRunState(RunState.CAUGHT);
            } else if (countdownTimer.isExpired()) {
                countdownTimer.expire();
            }

            // Update pickups (check if player contacts them)
            for (ItemPickup pickup : pickups) {
                pickup.update(student);
            }
        }

        // Clear screen (dark background)
        ScreenUtils.clear(0.1f, 0.12f, 0.15f, 1f);

        // 1. Draw entities using ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Central Hall goal zone on the right
        shapeRenderer.setColor(0.15f, 0.4f, 0.2f, 0.5f);
        shapeRenderer.rect(580, 0, 60, 480);

        // Draw Hidden Events (Statue & Tree)
        hiddenEventManager.draw(shapeRenderer);

        // Draw pickups
        for (ItemPickup pickup : pickups) {
            if (!pickup.isCollected()) {
                if (pickup.getItem() instanceof DistractionItem) {
                    shapeRenderer.setColor(Color.YELLOW);
                } else if (pickup.getItem() instanceof FeedItem) {
                    shapeRenderer.setColor(Color.ORANGE);
                } else {
                    shapeRenderer.setColor(Color.CYAN);
                }
                pickup.draw(shapeRenderer);
            }
        }

        // Fallback for entities who fail to load
        if (!goose.hasTexture()) goose.draw(shapeRenderer);
        if (!student.hasTexture()) student.draw(shapeRenderer);

        // Draw Health Bar (Colourblind accessible)
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(20, 440, 150, 16);
        if (colorBlindMode) {
            shapeRenderer.setColor(student.getHealthPercent() > 0.3f ? Color.SKY : Color.MAGENTA);
        } else {
            shapeRenderer.setColor(student.getHealthPercent() > 0.3f ? Color.GREEN : Color.RED);
        }
        shapeRenderer.rect(20, 440, (float) (150 * student.getHealthPercent()), 16);

        // Pause Menu Backdrop (QR_PAUSE_MENU < 0.5s activation)
        if (countdownTimer.getRunState().isPaused()) {
            shapeRenderer.setColor(0.05f, 0.08f, 0.12f, 0.92f);
            shapeRenderer.rect(130, 90, 380, 300);
        }

        shapeRenderer.end();

        // 1b. Draw animated sprites (goose first so the student renders on top)
        batch.begin();
        goose.draw(batch);
        student.draw(batch);
        batch.end();

        // 2. Draw Inventory HUD
        inventoryUI.render(shapeRenderer, batch, font);

        // 3. Draw HUD Text (Controls, Score, Timer, Status)
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + scoreSystem.getScore() + " [" + scoreSystem.getDifficulty().getName() + "]", 480, 460);
        font.draw(batch, "Health: " + student.getHealth() + "/" + student.getMaxHealth(), 20, 475);

        // Draw Timer with WarningLevel coloring & high-contrast mode
        WarningLevel level = countdownTimer.getWarningLevel();
        if (colorBlindMode) {
            font.setColor(level == WarningLevel.CRITICAL ? Color.MAGENTA : (level == WarningLevel.WARNING ? Color.CYAN : Color.SKY));
        } else {
            if (level == WarningLevel.CRITICAL) {
                font.setColor(Color.RED);
            } else if (level == WarningLevel.WARNING) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.GREEN);
            }
        }
        font.draw(batch, "Time: " + countdownTimer.getFormattedTime() + " [" + level + "]", 240, 475);
        font.draw(batch, "Secrets: " + hiddenEventManager.getEventsInteracted() + "/2", 400, 475);

        // Goal indicator
        font.setColor(colorBlindMode ? Color.SKY : Color.LIME);
        font.draw(batch, "CENTRAL HALL ->", 500, 240);

        if (student.hasUmbrella()) {
            font.setColor(Color.CYAN);
            font.draw(batch, "Umbrella: " + String.format("%.1fs", student.getUmbrellaTimeLeft()), 20, 400);
        }

        if (goose.isDistracted()) {
            font.setColor(Color.YELLOW);
            font.draw(batch, "Goose Distracted!", 500, 430);
        } else if (goose.isEating()) {
            font.setColor(Color.ORANGE);
            font.draw(batch, "Goose Eating!", 500, 430);
        }

        // Hidden event interaction prompt
        if (nearbyEvent != null && !nearbyEvent.isCompleted()) {
            font.setColor(Color.GOLD);
            font.draw(batch, "[E] " + nearbyEvent.getInteractionPrompt(), (float) nearbyEvent.getX() - 30, (float) nearbyEvent.getY() + 45);
        }

        // Notification banner
        if (notificationMessage != null) {
            font.setColor(Color.GOLD);
            font.draw(batch, notificationMessage, 140, 435);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "WASD: Move | 1-5: Slot | SPACE: Use | E: Interact | TAB: Diff | C: Colorblind | P: Pause | R: Reset", 10, 415);

        // Terminal State Messages & Pause Menu
        if (escapeObjective.isCompleted()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "ESCAPED! You reached Central Hall in time! Press 'R' to play again.", 100, 260);
        } else if (countdownTimer.isExpired()) {
            font.setColor(Color.RED);
            font.draw(batch, "TIME EXPIRED! You missed your lecture. Press 'R' to restart.", 110, 260);
        } else if (student.isDead()) {
            font.setColor(Color.RED);
            font.draw(batch, "CAUGHT! The geese defeated you. Press 'R' to restart.", 140, 260);
        } else if (countdownTimer.getRunState().isPaused()) {
            font.setColor(Color.GOLD);
            font.draw(batch, "=== PAUSE MENU ===", 250, 360);
            font.setColor(Color.WHITE);
            font.draw(batch, "[P] Resume Game", 200, 325);
            font.draw(batch, "[R] Restart Game", 200, 295);
            font.draw(batch, "[TAB] Difficulty: " + scoreSystem.getDifficulty().getName() + " (" + scoreSystem.getDifficulty().getBaseMultiplier() + "x)", 200, 265);
            font.draw(batch, "[C] Colourblind Mode: " + (colorBlindMode ? "ON (High Contrast)" : "OFF"), 200, 235);
            font.setColor(Color.CYAN);
            font.draw(batch, "Accessibility: Subtitles & Text Cues Active", 175, 195);
            font.setColor(Color.LIME);
            font.draw(batch, "Safety: Family-Friendly | Seizure Safe", 190, 165);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Press 'P' to resume playing", 230, 125);
        }

        batch.end();
        
        //space -> Shoot
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            player.shoot();
        }
        //WASD -> Move
        player.move(
				Gdx.input.isKeyPressed(Input.Keys.A),
				Gdx.input.isKeyPressed(Input.Keys.D),
				Gdx.input.isKeyPressed(Input.Keys.S),
				Gdx.input.isKeyPressed(Input.Keys.W), 
				Gdx.graphics.getDeltaTime());
				
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        student.dispose();
        goose.dispose();
    }
}
