package io.github.gegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import io.github.gegame.escape.*;
import io.github.gegame.inventory.*;

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

    private int score = 0;
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
        student = new Student(100, 240);
        goose = new Goose(500, 240);
        score = 0;

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

        // Demonstrate Observer pattern: Score listens to inventory events
        student.getInventory().addListener(new InventoryListener() {
            @Override
            public void onItemAdded(Item item) {
                if (item instanceof FoundItem) {
                    score += 100;
                    showNotification("Picked up rare item: " + item.getName() + " (+100 pts)!");
                } else {
                    score += 25;
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
        handleInput(dt);

        boolean isGameRunning = countdownTimer.getRunState().isRunning() && !student.isDead() && !escapeObjective.isCompleted();

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
                score += 500; // Bonus for escaping
                showNotification("ESCAPED! You reached Central Hall on time! (+500 pts)");
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

        // Draw Student and Goose
        student.draw(shapeRenderer);
        goose.draw(shapeRenderer);

        // Draw Health Bar
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(20, 440, 150, 16);
        shapeRenderer.setColor(student.getHealthPercent() > 0.3f ? Color.GREEN : Color.RED);
        shapeRenderer.rect(20, 440, (float) (150 * student.getHealthPercent()), 16);

        shapeRenderer.end();

        // 2. Draw Inventory HUD
        inventoryUI.render(shapeRenderer, batch, font);

        // 3. Draw HUD Text (Controls, Score, Timer, Status)
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + score, 540, 460);
        font.draw(batch, "Health: " + student.getHealth() + "/" + student.getMaxHealth(), 20, 475);

        // Draw Timer with WarningLevel coloring
        WarningLevel level = countdownTimer.getWarningLevel();
        if (level == WarningLevel.CRITICAL) {
            font.setColor(Color.RED);
        } else if (level == WarningLevel.WARNING) {
            font.setColor(Color.YELLOW);
        } else {
            font.setColor(Color.GREEN);
        }
        font.draw(batch, "Time: " + countdownTimer.getFormattedTime() + " [" + level + "]", 240, 475);

        // Goal indicator
        font.setColor(Color.LIME);
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

        // Notification banner
        if (notificationMessage != null) {
            font.setColor(Color.GOLD);
            font.draw(batch, notificationMessage, 140, 435);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "WASD: Move | 1-5: Slot | SPACE: Use | P: Pause | R: Restart", 20, 415);

        // Terminal State Messages
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
            font.setColor(Color.YELLOW);
            font.draw(batch, "GAME PAUSED - Press 'P' to resume", 200, 260);
        }

        batch.end();
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            resetGame();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (countdownTimer.getRunState().isRunning()) {
                countdownTimer.pause();
            } else if (countdownTimer.getRunState().isPaused()) {
                countdownTimer.start();
            }
        }

        if (!countdownTimer.getRunState().isRunning() || student.isDead() || escapeObjective.isCompleted()) {
            return;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            student.moveLeft(dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            student.moveRight(dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            student.moveUp(dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            student.moveDown(dt);
        }

        // Keep student inside window boundaries
        double clampedX = Math.max(0, Math.min(640 - student.getWidth(), student.getX()));
        double clampedY = Math.max(0, Math.min(480 - student.getHeight(), student.getY()));
        student.setPosition(clampedX, clampedY);

        // Slot selection keys (1 to 5)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) student.getInventory().setSelectedIndex(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) student.getInventory().setSelectedIndex(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) student.getInventory().setSelectedIndex(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) student.getInventory().setSelectedIndex(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) student.getInventory().setSelectedIndex(4);

        // Use selected item
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Item selected = student.getInventory().getSelected();
            if (selected != null) {
                if (selected instanceof DistractionItem) {
                    showNotification("Threw Seeds! Goose is distracted for 5s!");
                } else if (selected instanceof FeedItem) {
                    showNotification("Fed Goose Bread! Goose is eating for 2s!");
                } else if (selected instanceof FoundItem) {
                    showNotification("Used " + selected.getName() + "!");
                }
            }
            student.useSelectedItem(goose);
        }
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
