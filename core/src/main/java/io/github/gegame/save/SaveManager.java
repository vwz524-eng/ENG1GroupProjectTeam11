package io.github.gegame.save;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Manages game saving and loading following Risk Assessment R11:
 * "Write saves to a temp file first, keep backup save slots" to eliminate corruption risks.
 */
public class SaveManager {

    private final File saveDirectory;

    public SaveManager(File saveDirectory) {
        this.saveDirectory = saveDirectory != null ? saveDirectory : new File("saves");
        if (!this.saveDirectory.exists()) {
            this.saveDirectory.mkdirs();
        }
    }

    public SaveManager() {
        this(new File("saves"));
    }

    public File getSaveFile(int slot) {
        return new File(saveDirectory, "save_slot_" + slot + ".dat");
    }

    public File getTempFile(int slot) {
        return new File(saveDirectory, "save_slot_" + slot + ".tmp");
    }

    public File getBackupFile(int slot) {
        return new File(saveDirectory, "save_slot_" + slot + ".bak");
    }

    /**
     * Saves game state atomically by writing to a temporary file first,
     * maintaining a backup of previous save state (Mitigation for R11).
     *
     * @param slot slot number (e.g. 1, 2, 3)
     * @param data the game state data to persist
     * @return true if save completed successfully, false otherwise
     */
    public boolean saveGame(int slot, GameStateData data) {
        if (data == null || slot < 1) {
            return false;
        }

        File saveFile = getSaveFile(slot);
        File tempFile = getTempFile(slot);
        File backupFile = getBackupFile(slot);

        // 1. Write to temporary file first
        try (FileOutputStream fos = new FileOutputStream(tempFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
            oos.flush();
        } catch (IOException e) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return false;
        }

        // 2. Backup existing save if present
        if (saveFile.exists()) {
            try {
                Files.copy(saveFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                // Non-fatal, proceed with save
            }
        }

        // 3. Atomically rename/replace temp file to primary save file
        try {
            Files.move(tempFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (IOException e) {
            // Fallback for file systems not supporting atomic move
            try {
                Files.move(tempFile.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException ex) {
                return false;
            }
        }
    }

    /**
     * Loads game state from primary slot file. If corrupted or missing,
     * automatically recovers from backup slot file (R11 resilience).
     *
     * @param slot slot number
     * @return loaded GameStateData or null if unavailable
     */
    public GameStateData loadGame(int slot) {
        File saveFile = getSaveFile(slot);
        File backupFile = getBackupFile(slot);

        if (saveFile.exists()) {
            try (FileInputStream fis = new FileInputStream(saveFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (GameStateData) ois.readObject();
            } catch (Exception e) {
                // Primary save corrupted: attempt recovery from backup
            }
        }

        if (backupFile.exists()) {
            try (FileInputStream fis = new FileInputStream(backupFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                return (GameStateData) ois.readObject();
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public boolean hasSave(int slot) {
        return getSaveFile(slot).exists() || getBackupFile(slot).exists();
    }

    public boolean deleteSave(int slot) {
        boolean deleted = false;
        File save = getSaveFile(slot);
        File temp = getTempFile(slot);
        File backup = getBackupFile(slot);

        if (save.exists()) deleted |= save.delete();
        if (temp.exists()) temp.delete();
        if (backup.exists()) backup.delete();
        return deleted;
    }
}

