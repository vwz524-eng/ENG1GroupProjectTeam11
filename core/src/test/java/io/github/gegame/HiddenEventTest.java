package io.github.gegame;

import io.github.gegame.hiddenevent.*;
import io.github.gegame.inventory.Inventory;
import io.github.gegame.inventory.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class HiddenEventTest {

    private Player player;
    private Inventory inventory;
    private HiddenEventManager manager;

    @BeforeEach
    public void setUp() {
        player = new Player("TestStudent", 100, 100);
        inventory = player.getInventory();
        manager = new HiddenEventManager();
    }

    @Test
    public void testStatueEventCreationAndProperties() {
        LongBoiStatueEvent statue = new LongBoiStatueEvent(250, 250);
        assertEquals("statue_umbrella", statue.getEventId());
        assertEquals("STATUE", statue.getEventType());
        assertEquals("Rub Long Boi's beak", statue.getInteractionPrompt());
        assertFalse(statue.isDiscovered());
        assertFalse(statue.isCompleted());
        assertTrue(statue.canActivate());
        assertNotNull(statue.getOutcome());
    }

    @Test
    public void testStatueEventProximityTrigger() {
        LongBoiStatueEvent statue = new LongBoiStatueEvent(200, 200);

        // Within 65 radius
        assertTrue(statue.checkTrigger(220, 220));
        assertTrue(statue.checkTrigger(200, 200));

        // Outside radius
        assertFalse(statue.checkTrigger(400, 400));
    }

    @Test
    public void testStatueEventActivationAndUmbrellaUnlock() {
        LongBoiStatueEvent statue = new LongBoiStatueEvent(100, 100);
        int initialScore = player.getScore();

        EventOutcome outcome = statue.activate(player, inventory);

        assertNotNull(outcome);
        assertTrue(statue.isCompleted());
        assertTrue(statue.isDiscovered());
        assertFalse(statue.canActivate()); // Completed, cannot activate again

        // Score bonus awarded
        assertTrue(player.getScore() > initialScore);

        // Umbrella item unlocked and added to inventory
        Item umbrella = inventory.getItem("umbrella");
        assertNotNull(umbrella);
        assertTrue(umbrella instanceof UmbrellaItem);
        assertEquals(3, ((UmbrellaItem) umbrella).getUsesRemaining());
    }

    @Test
    public void testUmbrellaItemUsageAndStateTransitions() {
        UmbrellaItem umbrella = new UmbrellaItem();
        assertEquals(3, umbrella.getUsesRemaining());
        assertEquals(UmbrellaItem.UmbrellaState.AVAILABLE, umbrella.getState());
        assertTrue(umbrella.canUse());

        // Use 1
        umbrella.use(player);
        assertEquals(2, umbrella.getUsesRemaining());
        assertEquals(UmbrellaItem.UmbrellaState.PROTECTED, umbrella.getState());
        assertTrue(umbrella.canUse());

        // Use 2
        umbrella.use(player);
        assertEquals(1, umbrella.getUsesRemaining());
        assertTrue(umbrella.canUse());

        // Use 3 (final use)
        umbrella.use(player);
        assertEquals(0, umbrella.getUsesRemaining());
        assertEquals(UmbrellaItem.UmbrellaState.EXHAUSTED, umbrella.getState());
        assertFalse(umbrella.canUse());
        assertTrue(umbrella.isUsedUp());
    }

    @Test
    public void testTreeEventCreationAndProperties() {
        TreeAllergyMedEvent tree = new TreeAllergyMedEvent(400, 300);
        assertEquals("tree_allergy_med", tree.getEventId());
        assertEquals("TREE", tree.getEventType());
        assertEquals("Investigate tree", tree.getInteractionPrompt());
        assertFalse(tree.isDiscovered());
        assertFalse(tree.isCompleted());
        assertTrue(tree.canActivate());
    }

    @Test
    public void testTreeEventDoublesRemainingHealthUnderCap() {
        TreeAllergyMedEvent tree = new TreeAllergyMedEvent(100, 100);
        player.setHealth(8); // 8 health points

        EventOutcome outcome = tree.activate(player, inventory);
        assertNotNull(outcome);
        assertTrue(tree.isCompleted());

        // 8 health doubled -> 16
        assertEquals(16, player.getHealth());

        // Allergy medication item added to inventory
        Item med = inventory.getItem("allergy_medication");
        assertNotNull(med);
        assertTrue(med instanceof AllergyMedicationItem);
    }

    @Test
    public void testTreeEventDoublesHealthCappedAt20() {
        TreeAllergyMedEvent tree = new TreeAllergyMedEvent(100, 100);
        player.setHealth(14); // 14 doubled would be 28, but cap is 20

        tree.activate(player, inventory);
        assertEquals(20, player.getHealth());
    }

    @Test
    public void testAllergyMedicationItemDirectUsage() {
        AllergyMedicationItem med = new AllergyMedicationItem();
        player.setHealth(7);

        med.use(player);
        assertEquals(14, player.getHealth());
        assertEquals(AllergyMedicationItem.MedicationState.HEALTH_RESTORED, med.getState());
        assertTrue(med.isUsedUp());

        // Cannot use again once consumed
        med.use(player);
        assertEquals(14, player.getHealth());
    }

    @Test
    public void testHiddenEventManagerRegistrationAndProximity() {
        LongBoiStatueEvent statue = new LongBoiStatueEvent(200, 200);
        TreeAllergyMedEvent tree = new TreeAllergyMedEvent(500, 500);

        manager.registerEvent(statue);
        manager.registerEvent(tree);

        assertEquals(2, manager.getEvents().size());
        assertEquals(0, manager.getEventsInteracted());

        // Player near statue
        player.setX(190);
        player.setY(190);
        HiddenEvent nearby = manager.checkProximity(player);
        assertNotNull(nearby);
        assertEquals("statue_umbrella", nearby.getEventId());
        assertTrue(nearby.isDiscovered());

        // Player far away
        player.setX(50);
        player.setY(50);
        assertNull(manager.checkProximity(player));
    }

    @Test
    public void testHiddenEventManagerInteractAndCounter() {
        LongBoiStatueEvent statue = new LongBoiStatueEvent(100, 100);
        TreeAllergyMedEvent tree = new TreeAllergyMedEvent(400, 400);

        manager.registerEvent(statue);
        manager.registerEvent(tree);

        player.setX(100);
        player.setY(100);

        EventOutcome outcome = manager.interact(player);
        assertNotNull(outcome);
        assertEquals(1, manager.getEventsInteracted());
        assertTrue(statue.isCompleted());

        // Move to tree and interact
        player.setX(400);
        player.setY(400);
        EventOutcome treeOutcome = manager.interact(player);
        assertNotNull(treeOutcome);
        assertEquals(2, manager.getEventsInteracted());
        assertTrue(tree.isCompleted());
    }

    @Test
    public void testDiscoveryListenerCallbacks() {
        LongBoiStatueEvent statue = new LongBoiStatueEvent(100, 100);
        manager.registerEvent(statue);

        AtomicBoolean discoveredFired = new AtomicBoolean(false);
        AtomicBoolean activatedFired = new AtomicBoolean(false);

        manager.addListener(new DiscoveryListener() {
            @Override
            public void onEventDiscovered(HiddenEvent event) {
                discoveredFired.set(true);
            }

            @Override
            public void onEventActivated(HiddenEvent event, EventOutcome outcome) {
                activatedFired.set(true);
            }
        });

        player.setX(100);
        player.setY(100);
        manager.checkProximity(player);
        assertTrue(discoveredFired.get());

        manager.interact(player);
        assertTrue(activatedFired.get());
    }

    @Test
    public void testEventTriggerAndDiscoveryCondition() {
        EventTrigger trigger = new EventTrigger(5);
        assertFalse(trigger.check());
        trigger.update(3);
        assertFalse(trigger.check());
        trigger.update(3);
        assertTrue(trigger.check());

        DiscoveryCondition condition = new DiscoveryCondition(true, false, 1.0f);
        // Player inventory starts empty
        assertFalse(condition.evaluate(player, 10));
        // Add item to inventory
        player.getInventory().addItem(new UmbrellaItem());
        assertTrue(condition.evaluate(player, 10));
    }

    @Test
    public void testDiscoveryRewardGrant() {
        DiscoveryReward reward = new DiscoveryReward(200, "Secret Found!");
        int scoreBefore = player.getScore();
        reward.grant(player);
        assertEquals(scoreBefore + 200, player.getScore());
    }

    @Test
    public void testPlayerRestoreHealthMethods() {
        player.setHealth(10);
        player.restoreHealth(25);
        assertEquals(35, player.getHealth());

        player.restoreHealth();
        assertEquals(player.getMaxHealth(), player.getHealth());
    }
}

