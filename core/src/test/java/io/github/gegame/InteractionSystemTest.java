package io.github.gegame;

import io.github.gegame.interaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InteractionSystemTest {

    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("TestPlayer", 0, 0);
    }

    @Test
    public void testCooldownLifecycle() {
        Cooldown cooldown = new Cooldown(2.0);
        assertTrue(cooldown.isReady());
        assertEquals(0.0, cooldown.getRemainingTime());

        cooldown.start();
        assertFalse(cooldown.isReady());
        assertEquals(2.0, cooldown.getRemainingTime());

        cooldown.update(1.0);
        assertFalse(cooldown.isReady());
        assertEquals(1.0, cooldown.getRemainingTime(), 0.001);

        cooldown.update(1.0);
        assertTrue(cooldown.isReady());
        assertEquals(0.0, cooldown.getRemainingTime(), 0.001);

        // Reset
        cooldown.start();
        assertFalse(cooldown.isReady());
        cooldown.reset();
        assertTrue(cooldown.isReady());
    }

    @Test
    public void testInteractionOutcome() {
        InteractionOutcome success = InteractionOutcome.success("Great job", 100);
        assertTrue(success.isSuccessful());
        assertEquals("Great job", success.getMessage());
        assertEquals(100, success.getPointsAwarded());

        InteractionOutcome failure = InteractionOutcome.failure("Failed attempt");
        assertFalse(failure.isSuccessful());
        assertEquals("Failed attempt", failure.getMessage());
        assertEquals(0, failure.getPointsAwarded());
    }

    @Test
    public void testDistractionTriggerAndDismiss() {
        Distraction distraction = new Distraction(180.0);
        assertEquals(180.0, distraction.getRadius());
        assertFalse(distraction.isActive());

        distraction.trigger();
        assertTrue(distraction.isActive());

        distraction.dismiss();
        assertFalse(distraction.isActive());
    }

    @Test
    public void testFeedItemConsumption() {
        FeedItem feed = new FeedItem("Bread Loaf", 20, 2);
        assertEquals("Bread Loaf", feed.getName());
        assertEquals(20, feed.getNutritionValue());
        assertEquals(2, feed.getQuantity());

        assertTrue(feed.consume());
        assertEquals(1, feed.getQuantity());

        assertTrue(feed.consume());
        assertEquals(0, feed.getQuantity());

        assertFalse(feed.consume());
        assertEquals(0, feed.getQuantity());
    }

    @Test
    public void testFeedingActionExecutionAndCooldown() {
        FeedItem feed = new FeedItem("Seeds", 10, 1);
        FeedingAction action = new FeedingAction(10, feed, new Cooldown(2.0));

        assertTrue(action.canExecute());
        assertEquals(120.0, action.getRange());
        assertEquals(10, action.getFeedAmount());

        // First execution succeeds
        InteractionOutcome outcome1 = action.execute(player);
        assertTrue(outcome1.isSuccessful());
        assertEquals(50, outcome1.getPointsAwarded());
        assertEquals(50, player.getScore());
        assertFalse(action.canExecute());

        // Second execution during cooldown fails
        InteractionOutcome outcome2 = action.execute(player);
        assertFalse(outcome2.isSuccessful());
        assertEquals(50, player.getScore()); // score didn't change

        // Wait out cooldown
        action.getCooldown().update(2.5);
        assertTrue(action.canExecute());

        // Third execution fails because feed item has no remaining quantity
        InteractionOutcome outcome3 = action.execute(player);
        assertFalse(outcome3.isSuccessful());
        assertTrue(outcome3.getMessage().contains("No food available"));
    }

    @Test
    public void testDistractionActionExecution() {
        Distraction distraction = new Distraction(200.0);
        DistractionAction action = new DistractionAction(30.0, distraction, new Cooldown(1.0));

        assertFalse(distraction.isActive());
        assertTrue(action.canExecute());

        InteractionOutcome outcome = action.execute(player);
        assertTrue(outcome.isSuccessful());
        assertTrue(distraction.isActive());
        assertEquals(60, outcome.getPointsAwarded());
        assertEquals(60, player.getScore());
        assertFalse(action.canExecute());

        // Fails while on cooldown
        InteractionOutcome outcomeCooldown = action.execute(player);
        assertFalse(outcomeCooldown.isSuccessful());

        action.getCooldown().update(1.1);
        assertTrue(action.canExecute());
    }

    @Test
    public void testPlayerInteractMethod() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(0, player.getScore());

        FeedItem bread = new FeedItem("Bread", 10, 5);
        FeedingAction feedAction = new FeedingAction(10, bread, new Cooldown(0.5));
        DistractionAction distAction = new DistractionAction(40.0, new Distraction(150.0), new Cooldown(0.5));

        InteractionOutcome feedOutcome = player.interact(feedAction);
        assertTrue(feedOutcome.isSuccessful());
        assertEquals(50, player.getScore());
        assertTrue(player.getInteractions().contains(feedAction));

        InteractionOutcome distOutcome = player.interact(distAction);
        assertTrue(distOutcome.isSuccessful());
        assertEquals(130, player.getScore()); // 50 + 80
        assertTrue(player.getInteractions().contains(distAction));
        assertEquals(2, player.getInteractions().size());
    }

    @Test
    public void testStudentInheritsInteraction() {
        Student student = new Student(50, 50);
        student.setName("StudentPlayer");

        FeedItem bread = new FeedItem("Bread", 5, 1);
        FeedingAction action = new FeedingAction(5, bread, new Cooldown(0.1));

        InteractionOutcome outcome = student.interact(action);
        assertTrue(outcome.isSuccessful());
        assertEquals(25, student.getScore());
    }

    @Test
    public void testBridgeWithInventoryItems() {
        io.github.gegame.inventory.Bread bread = new io.github.gegame.inventory.Bread();
        FeedingAction feedingAction = new FeedingAction(bread);
        assertEquals(10, feedingAction.getFeedAmount());
        assertEquals("Bread", feedingAction.getFeedItem().getName());

        io.github.gegame.inventory.SeedBag seedBag = new io.github.gegame.inventory.SeedBag();
        DistractionAction distractionAction = new DistractionAction(seedBag);
        assertEquals(150.0, distractionAction.getDistraction().getRadius());

        InteractionOutcome outcome = player.interact(feedingAction);
        assertTrue(outcome.isSuccessful());
        assertEquals(50, player.getScore());
    }
}

