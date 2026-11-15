package io.github.gegame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;

import io.github.gegame.escape.CountdownTimer;
import io.github.gegame.escape.EscapeObjective;
import io.github.gegame.escape.RunState;
import io.github.gegame.escape.WarningLevel;
import io.github.gegame.hiddenevent.AllergyMedicationItem;
import io.github.gegame.hiddenevent.DiscoveryListener;
import io.github.gegame.hiddenevent.EventOutcome;
import io.github.gegame.hiddenevent.HiddenEvent;
import io.github.gegame.hiddenevent.HiddenEventManager;
import io.github.gegame.hiddenevent.LongBoiStatueEvent;
import io.github.gegame.hiddenevent.TreeAllergyMedEvent;
import io.github.gegame.hiddenevent.UmbrellaItem;
import io.github.gegame.interaction.ActionSpamFilter;
import io.github.gegame.inventory.Bread;
import io.github.gegame.inventory.DistractionItem;
import io.github.gegame.inventory.FeedItem;
import io.github.gegame.inventory.FoundItem;
import io.github.gegame.inventory.InventoryListener;
import io.github.gegame.inventory.InventoryUI;
import io.github.gegame.inventory.Item;
import io.github.gegame.inventory.ItemPickup;
import io.github.gegame.runlifecycle.Intro_screen;
import io.github.gegame.score.DifficultyLevel;
import io.github.gegame.score.ScoreSystem;

/**
 * Main game loop
 */
public class Main extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private CampusMap campusMap;
    private ClockTimer clockTimer;

    private Student student;
    private final List<Goose> geese = new ArrayList<>();
    private final List<ItemPickup> pickups = new ArrayList<>();
    private InventoryUI inventoryUI;
    private InventoryEffects inventoryEffects;
    private int itemsUsed = 0;
    private int currentSwarm = 0;
    private float swarmBannerTimer = 0f;
    private static final int TOTAL_SWARMS = 3;

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
    private boolean wasTouchingLockedCentralHall = false;

    private String notificationMessage = "Welcome! WASD to move, 1-5 to select, SPACE to use items";
    private Timer.Task clearNotificationTask;

    // Audio assets, could add menu option to modify these (remove final)?
    private static final float MUSIC_VOLUME = 0.25f;
    private static final float GOOSE_HONK_VOLUME = 0.25f;
    private static final float DUCK_QUACK_VOLUME = 1.0f;
    private Music backgroundMusic;
    private Music gooseHonkMusic;
    private Sound duckQuackSound;

    // Setup.
    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        clockTimer = new ClockTimer(250, 418, 0.3f);
        campusMap = new CampusMap();
        campusMap.create();
        inventoryEffects = new InventoryEffects();

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


    // Geese not ducks, we had a debate about how geese honk instead of quack but decided it wasn't worth changing
    private void playDuckQuack() {
        if (duckQuackSound != null) {
            duckQuackSound.play(DUCK_QUACK_VOLUME);
        }
    }

    // ---
    // Intro screen/gameplay state managers
    // ---
    // does this using Gdx's glViewports
    // --

    /**
     * Starts or restarts the (active) gameplay loop from the menu or the end screen
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
     * Resets the game state and returns to the intro screen
     */
    public void returnToIntroScreen() {
        resetGame();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setScreen(new Intro_screen());
    }

    private void resetGame() {
        // Need to free entities textures so there is no memory leak
        if (student != null) student.dispose();
        for (Goose g : geese) {
            g.dispose();
        }
        geese.clear();
        if (hiddenEventManager != null) hiddenEventManager.dispose();

        student = new Student(100, 240);
        if (inventoryEffects != null) inventoryEffects.reset();
        itemsUsed = 0;
        currentSwarm = 0;
        spawnSwarm(1);
        scoreSystem = new ScoreSystem(selectedDifficulty);
        student.setScore(0);
        wasTouchingLockedCentralHall = false;

        // Initialize countdown
        countdownTimer = new CountdownTimer(300);
        countdownTimer.start();
        escapeObjective = new EscapeObjective(true);

        pickups.clear();
        // SeedBag and Bread are in InitialInventory

        if (inventoryUI != null) inventoryUI.dispose();
        inventoryUI = new InventoryUI(student.getInventory());

        // Initialize Hidden Event Discovery aligned with the campus.tmx Long Boi & Tree
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

        // Score depends on the inventoryt events
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

        showNotification("Survive all 3 swarms (before the 5-minute timer expires)!");
    }

    private void spawnSwarm(int swarmNumber) {
        currentSwarm = swarmNumber;
        swarmBannerTimer = 3f; // Giant FEED THE GEESE!!! prompt
        int gooseCount = getGooseCountForDifficulty(selectedDifficulty) * (1 << (swarmNumber - 1));
        List<Vector2> lakeWorld = campusMap.getLakeCenters();
        if (lakeWorld.isEmpty()) {
            Gdx.app.error("Goose", "No lake rectangles found in the Tiled Collision layer");
            return;
        }

        double roundSpeed = swarmNumber == 1 ? 0.50
            : swarmNumber == 2 ? 0.60
            : 0.70;

        double speed = selectedDifficulty
            .getGooseSpeedModifier()
            .multiplier() * roundSpeed;

        for (int i = 0; i < gooseCount; i++) {
            Vector2 point = lakeWorld.get((i + swarmNumber - 1) % lakeWorld.size());
            double lakeX = point.x / WORLD_SCALE_X;
            double lakeY = point.y / WORLD_SCALE_Y;
            Goose goose = new Goose(lakeX - 16, lakeY - 16);
            goose.setHomeLake(lakeX, lakeY);
            goose.setSwarm(swarmNumber);
            goose.setSpeedMultiplier(speed);
            geese.add(goose);
        }
    }

    private int getGooseCountForDifficulty(DifficultyLevel difficulty) {
        if (difficulty == null) return 2;
        if ("Hard".equalsIgnoreCase(difficulty.getName())) return 3;
        if ("Normal".equalsIgnoreCase(difficulty.getName())) return 2;
        return 1;
    }

    private void updateSwarmProgress() {
        Iterator<Goose> iterator = geese.iterator();
        while (iterator.hasNext()) {
            Goose goose = iterator.next();
            if (goose.hasReturnedToLake()) {
                goose.dispose();
                iterator.remove();
            }
        }
        if (!geese.isEmpty()) return;
        if (currentSwarm < TOTAL_SWARMS) {
            spawnSwarm(currentSwarm + 1);
        } else if (!escapeObjective.isCompleted()) {
            escapeObjective.complete();
            countdownTimer.setRunState(RunState.ESCAPED);
            scoreSystem.addEscapeBonus(countdownTimer.getRemaining());
            scoreSystem.addTimeBonus(countdownTimer.getRemaining());
            student.setScore(scoreSystem.getScore());
            playDuckQuack();
            showNotification("All three swarms fed! You reached Central Hall in time!");
        }
    }

    private void resolveGooseCollisions() {
        final double minSeparation = 30.0;
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < geese.size(); i++) {
                Goose g1 = geese.get(i);
                if (g1 == null || !g1.isAvailableForFeeding()) continue;

                for (int j = i + 1; j < geese.size(); j++) {
                    Goose g2 = geese.get(j);
                    if (g2 == null || !g2.isAvailableForFeeding()) continue;

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
        if (countdownTimer.getRunState().isRunning()) {
            swarmBannerTimer = Math.max(0f, swarmBannerTimer - dt);
            inventoryEffects.update(dt);
        }

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
                if (swarmBannerTimer <= 0) student.checkGooseCollision(g);
            }
            updateSwarmProgress();

            // The goal unlocks automatically once the final swarm is finished
            double hitboxRight = student.getX() + STUDENT_HITBOX_OFFSET_X + STUDENT_HITBOX_WIDTH;
            double hitboxBottom = student.getY() + STUDENT_HITBOX_OFFSET_Y;
            double hitboxTop = hitboxBottom + STUDENT_HITBOX_HEIGHT;
            boolean touchingCentralHall = student.getX() >= 580
                || (hitboxRight >= 520 && hitboxTop >= 200 && hitboxBottom <= 301);
            if (touchingCentralHall && !wasTouchingLockedCentralHall && !escapeObjective.isCompleted()) {
                showNotification("Feed all three swarms before entering Central Hall!");
            }
            wasTouchingLockedCentralHall = touchingCentralHall;

            // Check defeat conditions
            if (student.isDead()) {
                countdownTimer.setRunState(RunState.CAUGHT);
                playDuckQuack();
            } else if (countdownTimer.isExpired()) {
                countdownTimer.expire();
                playDuckQuack();
            }

            // Update pickups
            for (ItemPickup pickup : pickups) {
                pickup.update(student);
            }
        }

        // Clear screen
        ScreenUtils.clear(0.1f, 0.12f, 0.15f, 1f);

        campusMap.render();


        // Draw entities
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //fallback for drawing hidden events
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

        // Draw hp to be colourblind accessible)
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1f);
        shapeRenderer.rect(20, 440, 150, 16);
        if (colorBlindMode) {
            shapeRenderer.setColor(student.getHealthPercent() > 0.3f ? Color.SKY : Color.MAGENTA);
        } else {
            shapeRenderer.setColor(student.getHealthPercent() > 0.3f ? Color.GREEN : Color.RED);
        }
        shapeRenderer.rect(20, 440, (float) (150 * student.getHealthPercent()), 16);

        if (countdownTimer.getRunState().isPaused()) {
            shapeRenderer.setColor(0.05f, 0.08f, 0.12f, 0.92f);
            shapeRenderer.rect(130, 90, 380, 300);
        }

        shapeRenderer.end();

        // Draw (goose first so the student renders on top)
        batch.begin();
        hiddenEventManager.draw(batch);
        for (Goose g : geese) {
            g.draw(batch);
        }
        student.draw(batch);
        inventoryEffects.render(batch, student);
        batch.end();

        // hunger bar
        if (!countdownTimer.getRunState().isPaused()) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (Goose g : geese) {
                g.drawHungerBar(shapeRenderer, colorBlindMode);
            }
            shapeRenderer.end();
        }

        // Inventory
        inventoryUI.render(shapeRenderer, batch, font);

        // the Text for the HUD (Controls, Score, Timer, Status)
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Score: " + scoreSystem.getScore() + " [" + scoreSystem.getDifficulty().getName() + "]", 480, 460);
        font.draw(batch, "Allergy Resistance: " + student.getHealth() + "/" + student.getMaxHealth(), 20, 475);

        // Draw Timer with WarningLevel coloring & high-contrast mode
        WarningLevel level = countdownTimer.getWarningLevel();
        if (colorBlindMode) {
            font.setColor(level == WarningLevel.CRITICAL ? Color.MAGENTA : (level == WarningLevel.WARNING ? Color.CYAN : Color.SKY));
        }
         else {
            switch (level) {
                case CRITICAL -> font.setColor(Color.RED);
                case WARNING -> font.setColor(Color.YELLOW);
                default -> font.setColor(Color.GREEN);
            }
        }
        clockTimer.drawTime(batch, countdownTimer.getRemaining(), countdownTimer.getRunState().isRunning());
        font.draw(batch, "Hidden Events: " + hiddenEventManager.getEventsInteracted() + "/2", 385, 475);
        font.setColor(Color.WHITE);
        font.draw(batch, "Items used: " + itemsUsed, 450, 393);
        font.draw(batch, "Swarm: " + Math.min(currentSwarm, TOTAL_SWARMS) + "/3", 450, 413);

        // scales for geesebar
        font.getData().setScale(0.65f);
        for (Goose g : geese) {
            if (!g.hasReturnedToLake()) {
                font.draw(batch, "Hunger " + g.getHunger() + "%", (float) g.getX() - 6, (float) Math.min(477, g.getY() + g.getHeight() + 23));
            }
        }
        font.getData().setScale(1f);

        // Draw the line for the obj - central hall
        font.setColor(colorBlindMode ? Color.SKY : Color.LIME);
        font.draw(batch, "CENTRAL HALL ->", 500, 240);

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

        // interaction prompt
        if (nearbyEvent != null && !nearbyEvent.isCompleted()) {
            font.setColor(Color.GOLD);
            font.draw(batch, "[E] " + nearbyEvent.getInteractionPrompt(), (float) nearbyEvent.getX() - 30, (float) nearbyEvent.getY() + 45);
        }

        if (notificationMessage != null) {
            font.setColor(Color.GOLD);
            font.draw(batch, notificationMessage, 140, 395);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "WASD: Move | 1-5: Slot | SPACE: Use | E: Interact | P: Pause | R: Reset", 10, 415);

        // statemessages and pause menu
        if (escapeObjective.isCompleted()) {
            font.setColor(Color.GREEN);
            font.draw(batch, "ESCAPED! You reached Central Hall in time! Press 'R' to play again.", 100, 260);
        } else if (countdownTimer.isExpired()) {
            font.setColor(Color.RED);
            font.draw(batch, "TIME EXPIRED! You missed your lecture. Press 'R' to restart.", 110, 260);
        } else if (student.isDead()) {
            font.setColor(Color.RED);
            font.draw(batch, "ALLERGIES! Resistance reached zero. Press R to restart.", 120, 260);
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

        if (swarmBannerTimer > 0f && !escapeObjective.isCompleted() && !student.isDead()) {
            font.getData().setScale(2.0f);
            font.setColor(Color.GOLD);
            font.draw(batch, "FEED THE GEESE!!!", 115, 345);
            font.getData().setScale(1f);
            font.setColor(Color.WHITE);
            font.draw(batch, "Swarm " + currentSwarm + "/3", 290, 305);
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

        // Slot selection keys (1-5)
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

        // Use selected item (SPACE) repeatedly while the game is running.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (!spamFilter.allowAction(gameTime)) return;
            Item selected = student.getInventory().getSelected();
            if (selected == null || selected.isUsedUp()) {
                showNotification("That slot is empty or the item has no uses left.");
                return;
            }
            if (selected instanceof Bread && ((Bread) selected).isOnCooldown()) {
                showNotification("Breadcrumbs ready in "
                    + Math.ceil(((Bread) selected).getCooldownRemaining() * 10) / 10.0 + "s");
                return;
            }
            if (student.useSelectedItem(geese)) {
                itemsUsed++;
                inventoryEffects.trigger(selected, student);
                if (selected instanceof Bread) {
                    showNotification("Breadcrumbs thrown! Keep feeding to lower Hunger.");
                    playDuckQuack();
                } else if (selected instanceof DistractionItem) {
                    showNotification("Seeds scattered! ");
                    playDuckQuack();
                } else if (selected instanceof AllergyMedicationItem) {
                    showNotification("Medication used! Resistance increased by 10 (max 20).");
                } else if (selected instanceof UmbrellaItem) {
                    showNotification("Umbrella open! Protected for 10 seconds.");
                }
            } else {
                showNotification("Move nearer to the geese to use this item.");
            }
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
            g.setSpeedMultiplier(difficulty.getGooseSpeedModifier().multiplier()
                * (currentSwarm <= 1 ? 1.0 : 2.0));
        }
        showNotification("Difficulty set to: " + difficulty.getName().toUpperCase()
            + " (" + difficulty.getBaseMultiplier() + "x score, goose speed "
            + difficulty.getGooseSpeedModifier().multiplier() + "x)!");
    }

    public DifficultyLevel getDifficulty() {
        return selectedDifficulty;
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
        if (clockTimer != null) clockTimer.dispose();
        for (Goose g : geese) {
            g.dispose();
        }

        if (hiddenEventManager != null) hiddenEventManager.dispose();
        if (inventoryUI != null) inventoryUI.dispose();
        if (inventoryEffects != null) inventoryEffects.dispose();
        if (campusMap != null) campusMap.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (gooseHonkMusic != null) gooseHonkMusic.dispose();
        if (duckQuackSound != null) duckQuackSound.dispose();
    }

}
