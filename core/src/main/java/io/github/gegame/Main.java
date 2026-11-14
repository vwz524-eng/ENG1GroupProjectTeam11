package io.github.gegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import io.github.gegame.escape.*;
import io.github.gegame.hiddenevent.*;
import io.github.gegame.interaction.ActionSpamFilter;
import io.github.gegame.inventory.*;
import io.github.gegame.runlifecycle.Intro_screen;
import io.github.gegame.score.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game loop demonstrating Student, Goose, ItemPickups, Inventory, and Timed Escape.
 */
public class Main extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private CampusMap campusMap;

    private Student student;
    private final List<Goose> geese = new ArrayList<>();
    private final List<ItemPickup> pickups = new ArrayList<>();
    private InventoryUI inventoryUI;

    // Timed Escape system components
    private CountdownTimer countdownTimer;
    private EscapeObjective escapeObjective;

    // Hidden Event Discovery system
    private HiddenEventManager hiddenEventManager;

    // Score & Difficulty system
    private ScoreSystem scoreSystem;
    private DifficultyLevel selectedDifficulty = DifficultyLevel.NORMAL;

    // Accessibility & Quality Requirements
    private boolean colorBlindMode = false;

    // Extreme activity / spam protection (Risk Assessment R21)
    private final ActionSpamFilter spamFilter = new ActionSpamFilter(0.12);
    private double gameTime = 0.0;

    // Map coordinate conversion (Main renders in 640x480 inside CampusMap's 1280x768 world)
    private static final float WORLD_SCALE_X = 1280f / 640f;
    private static final float WORLD_SCALE_Y = 768f / 480f;
    private static final float STUDENT_HITBOX_OFFSET_X = 20f;
    private static final float STUDENT_HITBOX_OFFSET_Y = 6f;
    private static final float STUDENT_HITBOX_WIDTH = 24f;
    private static final float STUDENT_HITBOX_HEIGHT = 20f;
    private static final int CENTRAL_HALL_UNLOCK_SECONDS = 60;
    private boolean wasTouchingLockedCentralHall = false;

    private String notificationMessage = "Welcome! WASD to move, 1-5 to select, SPACE to use items";
    private Timer.Task clearNotificationTask;

    // Audio assets
    private static final float MUSIC_VOLUME = 0.25f;
    private static final float GOOSE_HONK_VOLUME = 0.25f;
    private static final float DUCK_QUACK_VOLUME = 1.0f;
    private Music backgroundMusic;
    private Music gooseHonkMusic;
    private Sound duckQuackSound;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        campusMap = new CampusMap();
        campusMap.create();

        resetGame();
        initAudio();
        setScreen(new Intro_screen());
    }

    private void initAudio() {
        if (Gdx.audio == null || Gdx.files == null) {
            return;
        }
        try {
            if (Gdx.files.internal("audio/music/Central Hall Rush.mp3").exists()) {
                backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/Central Hall Rush.mp3"));
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(MUSIC_VOLUME);
                backgroundMusic.play();
            }
            if (Gdx.files.internal("audio/sfx/goose_honk.mp3").exists()) {
                gooseHonkMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/sfx/goose_honk.mp3"));
                gooseHonkMusic.setLooping(true);
                gooseHonkMusic.setVolume(GOOSE_HONK_VOLUME);
                gooseHonkMusic.play();
            }
            if (Gdx.files.internal("audio/sfx/duckquack.wav").exists()) {
                duckQuackSound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/duckquack.wav"));
            }
        } catch (Exception e) {
            Gdx.app.error("Audio", "Failed to load audio assets", e);
        }
    }

    private void playDuckQuack() {
        if (duckQuackSound != null) {
            duckQuackSound.play(DUCK_QUACK_VOLUME);
        }
    }

    /**
     * Starts or restarts active gameplay from a menu or end screen.
     */
    public void startGameplay() {
        resetGame();
        setScreen(null);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (campusMap != null) {
            campusMap.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(MUSIC_VOLUME);
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            }
        }
        if (gooseHonkMusic != null) {
            gooseHonkMusic.setVolume(GOOSE_HONK_VOLUME);
            if (!gooseHonkMusic.isPlaying()) {
                gooseHonkMusic.play();
            }
        }
    }

    /**
     * Resets the game state and returns to the intro screen.
     */
    public void returnToIntroScreen() {
        resetGame();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setScreen(new Intro_screen());
    }

    private void resetGame() {
        // Free the old entities' textures so restarting (R) doesn't leak GPU memory
        if (student != null) student.dispose();
        for (Goose g : geese) {
            g.dispose();
        }
        geese.clear();
        if (hiddenEventManager != null) hiddenEventManager.dispose();

        student = new Student(100, 240);
        spawnGeeseForDifficulty();
        scoreSystem = new ScoreSystem(selectedDifficulty);
        student.setScore(0);
        wasTouchingLockedCentralHall = false;

        // Initialize 5-minute timed escape countdown
        countdownTimer = new CountdownTimer(300);
        countdownTimer.start();
        escapeObjective = new EscapeObjective(true);

        pickups.clear();
        // SeedBag and Bread are in the starting inventory (InitialInventory);
        // Umbrella is only obtained from the Long Boi hidden event.

        inventoryUI = new InventoryUI(student.getInventory());

        // Initialize Hidden Event Discovery aligned with campus.tmx Long Boi & Tree
        hiddenEventManager = new HiddenEventManager();
        hiddenEventManager.registerEvent(new LongBoiStatueEvent(240, 360));
        hiddenEventManager.registerEvent(new TreeAllergyMedEvent(408, 130));

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

        showNotification("Escape to Central Hall after 1 minute (before the 5-minute timer expires)!");
    }

    private void spawnGeeseForDifficulty() {
        int gooseCount = getGooseCountForDifficulty(selectedDifficulty);
        float[][] spawnPoints = {
            {480f, 240f},
            {340f, 340f},
            {360f, 120f}
        };
        double speedMultiplier = selectedDifficulty.getGooseSpeedModifier().getMultiplier();
        for (int i = 0; i < gooseCount; i++) {
            Goose g = new Goose(spawnPoints[i][0], spawnPoints[i][1]);
            g.setSpeedMultiplier(speedMultiplier);
            geese.add(g);
        }
    }

    private int getGooseCountForDifficulty(DifficultyLevel difficulty) {
        if (difficulty == null) return 2;
        if ("Hard".equalsIgnoreCase(difficulty.getName())) return 3;
        if ("Normal".equalsIgnoreCase(difficulty.getName())) return 2;
        return 1;
    }

    /**
     * Resolves solid collision between geese so multiple geese do not stack on top of each other,
     * while leaving student-goose movement unobstructed.
     */
    private void resolveGooseCollisions() {
        final double minSeparation = 30.0;
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < geese.size(); i++) {
                Goose g1 = geese.get(i);
                if (g1 == null || g1.isDead()) continue;

                for (int j = i + 1; j < geese.size(); j++) {
                    Goose g2 = geese.get(j);
                    if (g2 == null || g2.isDead()) continue;

                    double dx = g2.centerX() - g1.centerX();
                    double dy = g2.centerY() - g1.centerY();
                    double dist = Math.hypot(dx, dy);
                    if (dist >= minSeparation) continue;

                    double nx = dist < 0.001 ? 1.0 : dx / dist;
                    double ny = dist < 0.001 ? 0.0 : dy / dist;
                    double overlap = minSeparation - dist;

                    boolean g1Eating = g1.isEating();
                    boolean g2Eating = g2.isEating();

                    if (g1Eating && !g2Eating) {
                        g2.setPosition(g2.getX() + nx * overlap, g2.getY() + ny * overlap);
                    } else if (g2Eating && !g1Eating) {
                        g1.setPosition(g1.getX() - nx * overlap, g1.getY() - ny * overlap);
                    } else {
                        double half = overlap * 0.5;
                        g1.setPosition(g1.getX() - nx * half, g1.getY() - ny * half);
                        g2.setPosition(g2.getX() + nx * half, g2.getY() + ny * half);
                    }

                    g1.setPosition(
                        Math.max(0, Math.min(640 - g1.getWidth(), g1.getX())),
                        Math.max(0, Math.min(480 - g1.getHeight(), g1.getY()))
                    );
                    g2.setPosition(
                        Math.max(0, Math.min(640 - g2.getWidth(), g2.getX())),
                        Math.max(0, Math.min(480 - g2.getHeight(), g2.getY()))
                    );
                }
            }
        }
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
        if (getScreen() != null) {
            super.render();
            return;
        }

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
            for (Goose g : geese) {
                g.update(dt, student);
            }
            resolveGooseCollisions();
            for (Goose g : geese) {
                student.checkGooseCollision(g);
            }

            // Win only after 1 minute has elapsed AND player touches Central Hall
            int elapsedSeconds = countdownTimer.getDuration() - countdownTimer.getRemaining();
            boolean centralHallUnlocked = elapsedSeconds >= CENTRAL_HALL_UNLOCK_SECONDS;
            double hitboxRight = student.getX() + STUDENT_HITBOX_OFFSET_X + STUDENT_HITBOX_WIDTH;
            double hitboxBottom = student.getY() + STUDENT_HITBOX_OFFSET_Y;
            double hitboxTop = hitboxBottom + STUDENT_HITBOX_HEIGHT;
            boolean touchingCentralHall = student.getX() >= 580
                || (hitboxRight >= 520 && hitboxTop >= 200 && hitboxBottom <= 301);
            if (touchingCentralHall) {
                if (centralHallUnlocked) {
                    escapeObjective.complete();
                    countdownTimer.setRunState(RunState.ESCAPED);
                    scoreSystem.addEscapeBonus(countdownTimer.getRemaining());
                    scoreSystem.addTimeBonus(countdownTimer.getRemaining());
                    student.setScore(scoreSystem.getScore());
                    playDuckQuack();
                    showNotification("ESCAPED! Reached Central Hall on time! (+" + scoreSystem.getScore() + " pts)");
                } else if (!wasTouchingLockedCentralHall) {
                    int secondsLeft = CENTRAL_HALL_UNLOCK_SECONDS - elapsedSeconds;
                    showNotification("Central Hall is locked! Survive " + secondsLeft + "s more to enter!");
                }
            }
            wasTouchingLockedCentralHall = touchingCentralHall && !centralHallUnlocked;

            // Check defeat conditions
            if (student.isDead()) {
                countdownTimer.setRunState(RunState.CAUGHT);
                playDuckQuack();
            } else if (countdownTimer.isExpired()) {
                countdownTimer.expire();
                playDuckQuack();
            }

            // Update pickups (check if player contacts them)
            for (ItemPickup pickup : pickups) {
                pickup.update(student);
            }
        }

        // Clear screen (dark background)
        ScreenUtils.clear(0.1f, 0.12f, 0.15f, 1f);

        // Display the campus map
        campusMap.render();


        // 1. Draw entities using ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw Hidden Events fallback (only used if textures couldn't be loaded)
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
        for (Goose g : geese) {
            if (!g.hasTexture()) g.draw(shapeRenderer);
        }
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

        // 1b. Draw world textures and animated sprites (goose first so the student renders on top)
        batch.begin();
        hiddenEventManager.draw(batch);
        for (Goose g : geese) {
            g.draw(batch);
        }
        student.draw(batch);
        batch.end();

        // 1c. Draw hunger bar above each living goose
        if (!countdownTimer.getRunState().isPaused()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Goose g : geese) {
                g.drawHungerBar(shapeRenderer, colorBlindMode);
            }
            shapeRenderer.end();
        }

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
        int elapsedSeconds = countdownTimer.getDuration() - countdownTimer.getRemaining();
        if (elapsedSeconds < CENTRAL_HALL_UNLOCK_SECONDS) {
            font.setColor(colorBlindMode ? Color.CYAN : Color.ORANGE);
            font.draw(batch, "CENTRAL HALL (" + (CENTRAL_HALL_UNLOCK_SECONDS - elapsedSeconds) + "s)", 465, 240);
        } else {
            font.setColor(colorBlindMode ? Color.SKY : Color.LIME);
            font.draw(batch, "CENTRAL HALL ->", 500, 240);
        }

        if (student.hasUmbrella()) {
            font.setColor(Color.CYAN);
            font.draw(batch, "Umbrella: " + String.format("%.1fs", student.getUmbrellaTimeLeft()), 20, 400);
        }

        boolean anyDistracted = false;
        boolean anyEating = false;
        for (Goose g : geese) {
            if (g.isDistracted()) anyDistracted = true;
            if (g.isEating()) anyEating = true;
        }
        if (anyDistracted) {
            font.setColor(Color.YELLOW);
            font.draw(batch, "Goose Distracted!", 500, 430);
        } else if (anyEating) {
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
        font.draw(batch, "WASD: Move | 1-5: Slot | SPACE: Use | E: Interact | P: Pause | R: Reset", 10, 415);

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
            font.draw(batch, "Difficulty: " + scoreSystem.getDifficulty().getName() + " (" + scoreSystem.getDifficulty().getBaseMultiplier() + "x)", 200, 265);
            font.draw(batch, "[C] Colourblind Mode: " + (colorBlindMode ? "ON (High Contrast)" : "OFF"), 200, 235);
            font.setColor(Color.CYAN);
            font.draw(batch, "Accessibility: Subtitles & Text Cues Active", 175, 195);
            font.setColor(Color.LIME);
            font.draw(batch, "Safety: Family-Friendly | Seizure Safe", 190, 165);
            font.setColor(Color.LIGHT_GRAY);
            font.draw(batch, "Press 'P' to resume playing", 230, 125);
        }

        batch.end();
    }

    private boolean canStudentMoveTo(double batchX, double batchY) {
        if (campusMap == null) {
            return true;
        }
        Rectangle worldBounds = new Rectangle(
            (float) ((batchX + STUDENT_HITBOX_OFFSET_X) * WORLD_SCALE_X),
            (float) ((batchY + STUDENT_HITBOX_OFFSET_Y) * WORLD_SCALE_Y),
            STUDENT_HITBOX_WIDTH * WORLD_SCALE_X,
            STUDENT_HITBOX_HEIGHT * WORLD_SCALE_Y
        );
        return campusMap.canMoveTo(worldBounds);
    }

    private void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            returnToIntroScreen();
            return;
        }

        // Colourblind mode can only be toggled while paused
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) && countdownTimer.getRunState().isPaused()) {
            toggleColorBlindMode();
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

        double prevX = student.getX();
        double prevY = student.getY();

        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            student.moveLeft(dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            student.moveRight(dt);
        }
        double clampedX = Math.max(0, Math.min(640 - student.getWidth(), student.getX()));
        if (canStudentMoveTo(clampedX, prevY)) {
            student.setPosition(clampedX, prevY);
        } else {
            student.setPosition(prevX, prevY);
        }

        double resolvedX = student.getX();
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            student.moveUp(dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            student.moveDown(dt);
        }
        double clampedY = Math.max(0, Math.min(480 - student.getHeight(), student.getY()));
        if (canStudentMoveTo(resolvedX, clampedY)) {
            student.setPosition(resolvedX, clampedY);
        } else {
            student.setPosition(resolvedX, prevY);
        }

        // Slot selection keys (1 to 5)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) student.getInventory().setSelectedIndex(0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) student.getInventory().setSelectedIndex(1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) student.getInventory().setSelectedIndex(2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) student.getInventory().setSelectedIndex(3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) student.getInventory().setSelectedIndex(4);

        // Interact with nearby Hidden Event (E key) with spam protection (Risk Assessment R21)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (!spamFilter.allowAction(gameTime)) return;
            HiddenEvent nearby = hiddenEventManager.checkProximity(student);
            if (nearby != null && nearby.canActivate()) {
                EventOutcome outcome = hiddenEventManager.interact(student);
                if (outcome != null) {
                    showNotification(outcome.getMessage());
                }
                return;
            }
        }

        // Use selected item (SPACE key) with spam protection (Risk Assessment R21)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!spamFilter.allowAction(gameTime)) return;
            Item selected = student.getInventory().getSelected();
            if (selected != null && !selected.isUsedUp()) {
                if (selected instanceof Bread && ((Bread) selected).isOnCooldown()) {
                    int secs = (int) Math.ceil(((Bread) selected).getCooldownRemaining());
                    showNotification("Bread is recharging! " + secs + "s left!");
                    return;
                }
                if (selected instanceof DistractionItem) {
                    showNotification("Threw Seeds! Goose is distracted for 5s!");
                    playDuckQuack();
                } else if (selected instanceof FeedItem) {
                    showNotification("Fed Goose Bread! Goose is eating for 2s!");
                    playDuckQuack();
                } else if (selected instanceof FoundItem) {
                    if (selected instanceof AllergyMedicationItem) {
                        showNotification("Used Medication! Healed +50 HP!");
                    } else {
                        showNotification("Used " + selected.getName() + "!");
                    }
                }
            }
            student.useSelectedItem(geese);
        }
    }

    /**
     * Sets the active difficulty level and scales goose speed and score multipliers accordingly.
     *
     * @param difficulty the target difficulty level
     */
    public void setDifficulty(DifficultyLevel difficulty) {
        if (difficulty == null) return;
        this.selectedDifficulty = difficulty;
        if (scoreSystem != null) {
            scoreSystem.setDifficulty(difficulty);
        }
        for (Goose g : geese) {
            g.setSpeedMultiplier(difficulty.getGooseSpeedModifier().getMultiplier());
        }
        showNotification("Difficulty set to: " + difficulty.getName().toUpperCase()
            + " (" + difficulty.getBaseMultiplier() + "x score, goose speed "
            + difficulty.getGooseSpeedModifier().getMultiplier() + "x)!");
    }

    public DifficultyLevel getDifficulty() {
        return selectedDifficulty;
    }

    /**
     * Cycles through difficulty levels: Easy -> Normal -> Hard -> Easy.
     */
    public void cycleDifficulty() {
        DifficultyLevel current = selectedDifficulty != null ? selectedDifficulty : scoreSystem.getDifficulty();
        if ("Easy".equalsIgnoreCase(current.getName())) {
            setDifficulty(DifficultyLevel.NORMAL);
        } else if ("Normal".equalsIgnoreCase(current.getName())) {
            setDifficulty(DifficultyLevel.HARD);
        } else {
            setDifficulty(DifficultyLevel.EASY);
        }
    }

    /**
     * Toggles colourblind high-contrast visual display mode (QR_ACCESSIBILITY).
     */
    public void toggleColorBlindMode() {
        this.colorBlindMode = !this.colorBlindMode;
        showNotification("Colourblind Mode: " + (colorBlindMode ? "ENABLED (High Contrast)" : "DISABLED"));
    }

    public boolean isColorBlindMode() {
        return colorBlindMode;
    }

    public void setColorBlindMode(boolean mode) {
        this.colorBlindMode = mode;
    }


    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        if (campusMap != null) {
            campusMap.resize(width, height);
        }
    }
    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (student != null) student.dispose();
        for (Goose g : geese) {
            g.dispose();
        }
        if (hiddenEventManager != null) hiddenEventManager.dispose();
        if (campusMap != null) campusMap.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (gooseHonkMusic != null) gooseHonkMusic.dispose();
        if (duckQuackSound != null) duckQuackSound.dispose();
    }
}
