package io.github.gegame;

import io.github.gegame.score.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreAndDifficultyTest {

    private ScoreSystem scoreSystem;

    @BeforeEach
    public void setUp() {
        scoreSystem = new ScoreSystem(DifficultyLevel.NORMAL);
    }

    @Test
    public void testScoreSystemInitialization() {
        assertEquals(0, scoreSystem.getScore());
        assertEquals("Normal", scoreSystem.getDifficulty().getName());
        assertEquals(1.0f, scoreSystem.getDifficulty().getBaseMultiplier(), 0.001f);
        assertNotNull(scoreSystem.getTimeBonus());
        assertNotNull(scoreSystem.getDiscoveryBonus());
        assertNotNull(scoreSystem.getEscapeBonus());
        assertNotNull(scoreSystem.getPenalty());
    }

    @Test
    public void testTimeBonusCalculationAndApplication() {
        TimeBonus timeBonus = new TimeBonus(3);
        assertEquals(300, timeBonus.calculate(100));
        assertEquals(0, timeBonus.calculate(0));
        assertEquals(0, timeBonus.calculate(-10));

        // Adding via ScoreSystem
        scoreSystem.addTimeBonus(150); // 150 * 2 = 300
        assertEquals(300, scoreSystem.getScore());

        // Default no-arg method
        scoreSystem.addTimeBonus();
        assertTrue(scoreSystem.getScore() > 300);
    }

    @Test
    public void testDiscoveryBonusDeduplication() {
        DiscoveryBonus discovery = new DiscoveryBonus(150);
        assertEquals(150, discovery.award("statue_umbrella"));
        // Second claim for same discovery gives 0
        assertEquals(0, discovery.award("statue_umbrella"));
        // Different discovery gives full points
        assertEquals(150, discovery.award("tree_allergy_med"));

        // Test through ScoreSystem
        scoreSystem.addDiscoveryBonus("secret_A");
        assertEquals(150, scoreSystem.getScore());
        scoreSystem.addDiscoveryBonus("secret_A"); // Duplicate ignored
        assertEquals(150, scoreSystem.getScore());
        scoreSystem.addDiscoveryBonus("secret_B");
        assertEquals(300, scoreSystem.getScore());
    }

    @Test
    public void testEscapeBonusWithSpeedBonus() {
        EscapeBonus escape = new EscapeBonus(500);
        assertEquals(500, escape.award(0));
        assertEquals(620, escape.award(120)); // 500 base + 120 speed bonus

        scoreSystem.addEscapeBonus(80); // 500 + 80 = 580
        assertEquals(580, scoreSystem.getScore());
    }

    @Test
    public void testPenaltyApplicationAndZeroFloor() {
        scoreSystem.addPoints(50, "Initial score");
        assertEquals(50, scoreSystem.getScore());

        scoreSystem.applyPenalty("Goose collision");
        assertEquals(30, scoreSystem.getScore()); // 50 - 20 = 30

        scoreSystem.applyPenalty("Another collision");
        assertEquals(10, scoreSystem.getScore()); // 30 - 20 = 10

        // Score cannot drop below 0
        scoreSystem.applyPenalty("Third collision");
        assertEquals(0, scoreSystem.getScore());
    }

    @Test
    public void testDifficultyModifiers() {
        TimerModifier timerMod = new TimerModifier(1.2f);
        assertEquals(360, timerMod.modify(300));

        GooseSpeedModifier gooseMod = new GooseSpeedModifier(1.5f);
        assertEquals(150.0f, gooseMod.modify(100.0f), 0.001f);

        EventFrequencyModifier freqMod = new EventFrequencyModifier(0.8f);
        assertEquals(8.0f, freqMod.modify(10.0f), 0.001f);
    }

    @Test
    public void testDifficultyLevelScalingOnBonuses() {
        ScoreSystem hardScore = new ScoreSystem(DifficultyLevel.HARD); // 1.5x multiplier
        hardScore.addDiscoveryBonus("hard_secret"); // 150 * 1.5 = 225
        assertEquals(225, hardScore.getScore());

        ScoreSystem easyScore = new ScoreSystem(DifficultyLevel.EASY); // 0.8x multiplier
        easyScore.addDiscoveryBonus("easy_secret"); // 150 * 0.8 = 120
        assertEquals(120, easyScore.getScore());
    }

    @Test
    public void testGooseDifficultySwarms() {
        // Swarm 1: Derwent Campus
        GooseDifficulty swarm1 = GooseDifficulty.forSwarm(1);
        assertEquals(2, swarm1.getBreadcrumbsRequired());
        assertEquals(1.0f, swarm1.getSpeedMultiplier(), 0.001f);

        // Swarm 2: Long Boi Statue
        GooseDifficulty swarm2 = GooseDifficulty.forSwarm(2);
        assertEquals(4, swarm2.getBreadcrumbsRequired());
        assertEquals(2.0f, swarm2.getSpeedMultiplier(), 0.001f);

        // Swarm 3: Central Hall Tree
        GooseDifficulty swarm3 = GooseDifficulty.forSwarm(3);
        assertEquals(6, swarm3.getBreadcrumbsRequired());
        assertEquals(2.0f, swarm3.getSpeedMultiplier(), 0.001f);
    }

    @Test
    public void testScoreListenerNotifications() {
        AtomicInteger reportedNewScore = new AtomicInteger(-1);
        AtomicBoolean reportedDifficulty = new AtomicBoolean(false);

        scoreSystem.addListener(new ScoreListener() {
            @Override
            public void onScoreChanged(int oldScore, int newScore, String reason) {
                reportedNewScore.set(newScore);
            }

            @Override
            public void onDifficultyChanged(DifficultyLevel newDifficulty) {
                reportedDifficulty.set(true);
            }
        });

        scoreSystem.addPoints(100, "Test");
        assertEquals(100, reportedNewScore.get());

        scoreSystem.setDifficulty(DifficultyLevel.HARD);
        assertTrue(reportedDifficulty.get());
    }

    @Test
    public void testScoreReset() {
        scoreSystem.addPoints(250, "Some Points");
        scoreSystem.addDiscoveryBonus("secret_reset");
        assertTrue(scoreSystem.getScore() > 0);

        scoreSystem.reset();
        assertEquals(0, scoreSystem.getScore());

        // Can claim discovery again after reset
        scoreSystem.addDiscoveryBonus("secret_reset");
        assertEquals(150, scoreSystem.getScore());
    }

    @Test
    public void testDynamicDifficultySwitching() {
        Goose goose = new Goose(100, 100);
        assertEquals(90.0, goose.getSpeed(), 0.001); // base speed 90 * 1.0

        // Switch to EASY
        scoreSystem.setDifficulty(DifficultyLevel.EASY);
        goose.setSpeedMultiplier(scoreSystem.getDifficulty().getGooseSpeedModifier().getMultiplier());
        assertEquals(0.8f, scoreSystem.getDifficulty().getBaseMultiplier(), 0.001f);
        assertEquals(72.0, goose.getSpeed(), 0.001); // 90 * 0.8 = 72

        // Switch to HARD
        scoreSystem.setDifficulty(DifficultyLevel.HARD);
        goose.setSpeedMultiplier(scoreSystem.getDifficulty().getGooseSpeedModifier().getMultiplier());
        assertEquals(1.5f, scoreSystem.getDifficulty().getBaseMultiplier(), 0.001f);
        assertEquals(135.0, goose.getSpeed(), 0.001); // 90 * 1.5 = 135
    }
}

