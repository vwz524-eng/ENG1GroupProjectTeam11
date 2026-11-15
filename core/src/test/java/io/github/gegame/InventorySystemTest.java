package io.github.gegame;

import io.github.gegame.inventory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventorySystemTest {

    private Inventory inventory;
    private Player player;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory(3);
        player = new Player(0, 0, 32, 32);
    }

    @Test
    public void testItemProperties() {
        Item item = new DistractionItem("seed1", "Seeds", "Distracts geese", 120.0, 4.0);
        assertEquals("seed1", item.getId());
        assertEquals("Seeds", item.getName());
        assertEquals("Distracts geese", item.getDescription());
        assertFalse(item.isUsedUp());
        assertEquals(1, item.getQuantity());

        item.consume();
        assertTrue(item.isUsedUp());
        assertEquals(0, item.getQuantity());
    }

    @Test
    public void testItemEquality() {
        Item item1 = new DistractionItem("item_a", "Name A", "Desc A", 50, 2);
        Item item2 = new DistractionItem("item_a", "Name A", "Desc A", 50, 2);
        Item item3 = new DistractionItem("item_b", "Name B", "Desc B", 50, 2);

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    public void testDistractionItem() {
        DistractionItem distraction = new DistractionItem("seed", "Seed", "Seeds", 200.0, 6.0);
        assertEquals(200.0, distraction.getRadius());
        assertEquals(6.0, distraction.getDuration());

        Goose goose = new Goose(100, 100);
        assertFalse(goose.isDistracted());

        distraction.use(player, goose);
        assertTrue(goose.isDistracted());
        assertTrue(distraction.isUsedUp());
    }

    @Test
    public void testFeedItem() {
        FeedItem bread = new FeedItem("bread", "Bread", "Loaf of bread", 15);
        assertEquals(15, bread.getFeedAmount());

        Goose goose = new Goose(100, 100);
        assertFalse(goose.isEating());

        bread.use(player, goose);
        assertTrue(goose.isEating());
        assertEquals(15, 30 - 15); // Goose starting health is 30, takes 15 damage
        assertTrue(bread.isUsedUp());
    }

    @Test
    public void testFoundItem() {
        FoundItem found = new FoundItem("exam_sheet", "Exam Sheet", "Course exam paper");
        assertFalse(found.isCollected());

        found.setCollected(true);
        assertTrue(found.isCollected());

        Student student = new Student(0, 0);
        FoundItem umbrella = new FoundItem("umbrella", "Umbrella", "Weather protection", true);
        assertFalse(student.hasUmbrella());
        umbrella.use(student);
        assertTrue(student.hasUmbrella());
        assertTrue(umbrella.isUsedUp());
    }

    @Test
    public void testInventoryCapacityAndAdd() {
        Item item1 = new SeedBag();
        Item item2 = new Bread();
        Item item3 = new FoundItem("key", "Key", "A golden key");
        Item item4 = new FoundItem("coin", "Coin", "A silver coin");

        assertFalse(inventory.isFull());
        assertEquals(0, inventory.getSize());

        assertTrue(inventory.addItem(item1));
        assertTrue(inventory.addItem(item2));
        assertTrue(inventory.addItem(item3));

        assertEquals(3, inventory.getSize());
        assertTrue(inventory.isFull());

        // Cannot add beyond capacity
        assertFalse(inventory.addItem(item4));
        // Cannot add null
        assertFalse(inventory.addItem(null));
        assertEquals(3, inventory.getSize());
    }

    @Test
    public void testInventoryRemoveAndHas() {
        Item item1 = new SeedBag();
        Item item2 = new Bread();

        inventory.addItem(item1);
        inventory.addItem(item2);

        assertTrue(inventory.hasItem(item1));
        assertTrue(inventory.hasItem("seed_bag"));
        assertTrue(inventory.hasItem(item2));
        assertTrue(inventory.hasItem("bread"));
        assertFalse(inventory.hasItem("unknown_id"));

        assertTrue(inventory.removeItem(item1));
        assertFalse(inventory.hasItem(item1));
        assertFalse(inventory.hasItem("seed_bag"));
        assertEquals(1, inventory.getSize());

        // Removing non-existent item
        assertFalse(inventory.removeItem(item1));
        assertFalse(inventory.removeItem(null));
    }

    @Test
    public void testInventorySlotsAndSelection() {
        inventory.setItem(0, new SeedBag());
        inventory.setItem(1, new Bread());

        assertEquals("seed_bag", inventory.getItem(0).getId());
        assertEquals("bread", inventory.getItem(1).getId());
        assertNull(inventory.getItem(2));

        inventory.setSelectedIndex(0);
        assertEquals("seed_bag", inventory.getSelected().getId());

        inventory.setSelectedIndex(1);
        assertEquals("bread", inventory.getSelected().getId());

        inventory.clear();
        assertEquals(0, inventory.getSize());
        assertTrue(inventory.isEmpty());
    }

    @Test
    public void testInitialInventoryPopulate() {
        Inventory inv = new Inventory(5);
        assertTrue(inv.isEmpty());

        InitialInventory initial = new InitialInventory();
        initial.populate(inv);

        assertFalse(inv.isEmpty());
        assertTrue(inv.hasItem("seed_bag"));
        assertTrue(inv.hasItem("bread"));
        assertNotNull(inv.getItem(0));
        assertNotNull(inv.getItem(1));
    }

    @Test
    public void testItemPickupSuccess() {
        FoundItem key = new FoundItem("key", "Key", "Gold key");
        ItemPickup pickup = new ItemPickup(key, 50, 50);

        assertFalse(pickup.isCollected());
        assertFalse(player.getInventory().hasItem("key"));

        pickup.onPlayerContact(player);

        assertTrue(pickup.isCollected());
        assertTrue(player.getInventory().hasItem("key"));
        assertTrue(key.isCollected());

        // Contacting again shouldn't duplicate
        pickup.onPlayerContact(player);
        assertEquals(1, player.getInventory().getSize());
    }

    @Test
    public void testItemPickupWhenInventoryFull() {
        Inventory tinyInventory = new Inventory(1);
        tinyInventory.addItem(new Bread());
        player.setInventory(tinyInventory);

        Item key = new FoundItem("key", "Key", "Gold key");
        ItemPickup pickup = new ItemPickup(key, 50, 50);

        assertFalse(pickup.pickUp(tinyInventory));
        assertFalse(pickup.isCollected());

        pickup.onPlayerContact(player);
        assertFalse(pickup.isCollected());
        assertFalse(player.getInventory().hasItem("key"));
    }

    @Test
    public void testStudentInitialInventoryAndUseItem() {
        Student student = new Student(0, 0);
        Goose goose = new Goose(50, 50);

        assertNotNull(student.getInventory());
        assertTrue(student.getInventory().hasItem("seed_bag"));
        assertTrue(student.getInventory().hasItem("bread"));

        // Use selected item (slot 0: SeedBag)
        student.getInventory().setSelectedIndex(0);
        student.useSelectedItem(goose);
        assertTrue(goose.isDistracted());

        // Switch to slot 1: Bread and use
        student.getInventory().setSelectedIndex(1);
        student.useSelectedItem(goose);
        assertTrue(goose.isEating());
    }

    @Test
    public void testPlayerHealthAndDamage() {
        assertEquals(100, player.getHealth());
        player.takeDamage(30);
        assertEquals(70, player.getHealth());
        player.heal(10);
        assertEquals(80, player.getHealth());
        player.heal(100);
        assertEquals(100, player.getHealth());
        player.takeDamage(200);
        assertEquals(0, player.getHealth());
        assertTrue(player.isDead());
    }

    @Test
    public void testInventoryListenerObserverPattern() {
        final int[] score = {0};
        final String[] lastAction = {""};

        InventoryListener listener = new InventoryListener() {
            @Override
            public void onItemAdded(Item item) {
                lastAction[0] = "added:" + item.getId();
                score[0] += 50;
            }

            @Override
            public void onItemRemoved(Item item) {
                lastAction[0] = "removed:" + item.getId();
            }

            @Override
            public void onItemSelected(Item item, int index) {
                lastAction[0] = "selected:" + (item != null ? item.getId() : "null");
            }
        };

        inventory.addListener(listener);

        Item bread = new Bread();
        inventory.addItem(bread);
        assertEquals("added:bread", lastAction[0]);
        assertEquals(50, score[0]);

        inventory.setSelectedIndex(0);
        assertEquals("selected:bread", lastAction[0]);

        inventory.removeItem(bread);
        assertEquals("removed:bread", lastAction[0]);
    }
}

