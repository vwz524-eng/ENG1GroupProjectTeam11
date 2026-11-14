package io.github.gegame;

import io.github.gegame.save.GameStateData;
import io.github.gegame.save.SaveManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SaveManagerTest {

    @TempDir
    Path tempDir;

    private SaveManager saveManager;

    @BeforeEach
    public void setUp() {
        saveManager = new SaveManager(tempDir.toFile());
    }

    @Test
    public void testSaveAndLoadSuccess() {
        GameStateData state = new GameStateData(120.5, 240.0, 80, 450, 210, "Normal");
        state.getInventoryItemIds().add("bread");
        state.getInventoryItemIds().add("seed_bag");
        state.getDiscoveredEventIds().add("statue_umbrella");

        assertTrue(saveManager.saveGame(1, state));
        assertTrue(saveManager.hasSave(1));

        GameStateData loaded = saveManager.loadGame(1);
        assertNotNull(loaded);
        assertEquals(120.5, loaded.getPlayerX(), 0.001);
        assertEquals(80, loaded.getPlayerHealth());
        assertEquals(450, loaded.getPlayerScore());
        assertEquals(210, loaded.getRemainingTime());
        assertEquals("Normal", loaded.getDifficultyName());
        assertEquals(2, loaded.getInventoryItemIds().size());
        assertEquals(1, loaded.getDiscoveredEventIds().size());
    }

    @Test
    public void testBackupCreationOnSubsequentSave() {
        GameStateData state1 = new GameStateData(100, 100, 100, 100, 300, "Normal");
        saveManager.saveGame(1, state1);

        File backup = saveManager.getBackupFile(1);
        assertFalse(backup.exists()); // No backup on initial save

        GameStateData state2 = new GameStateData(200, 200, 90, 250, 250, "Hard");
        saveManager.saveGame(1, state2);

        assertTrue(backup.exists()); // Backup created from previous save
    }

    @Test
    public void testCorruptionRecoveryFromBackup() throws Exception {
        GameStateData state1 = new GameStateData(150, 150, 75, 300, 200, "Easy");
        saveManager.saveGame(1, state1);

        GameStateData state2 = new GameStateData(250, 250, 50, 600, 100, "Hard");
        saveManager.saveGame(1, state2);

        // Intentionally corrupt primary save file with garbage bytes
        File saveFile = saveManager.getSaveFile(1);
        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write("CORRUPTED_GARBAGE_DATA".getBytes());
        }

        // SaveManager should catch corruption and recover previous state from backup
        GameStateData recovered = saveManager.loadGame(1);
        assertNotNull(recovered);
        assertEquals(150, recovered.getPlayerX(), 0.001);
        assertEquals(75, recovered.getPlayerHealth());
        assertEquals(300, recovered.getPlayerScore());
        assertEquals("Easy", recovered.getDifficultyName());
    }
}

