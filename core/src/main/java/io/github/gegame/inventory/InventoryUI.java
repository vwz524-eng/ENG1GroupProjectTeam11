package io.github.gegame.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renders the inventory HUD (hotbar slots, item labels, and active selection) on screen.
 */
public class InventoryUI {
    private final Inventory inventory;
    private float slotSize = 52f;
    private float slotSpacing = 8f;
    private float x = 150f;
    private float y = 20f;

    public InventoryUI(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Renders the hotbar slots and contents.
     *
     * @param shapeRenderer renderer for slot boxes
     * @param batch sprite batch for text rendering
     * @param font font for slot numbers and item names
     */
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        if (inventory == null) return;

        int capacity = inventory.getCapacity();
        int selected = inventory.getSelectedIndex();

        // 1. Draw Slot Backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < capacity; i++) {
            float slotX = x + i * (slotSize + slotSpacing);
            shapeRenderer.setColor(0.12f, 0.12f, 0.16f, 0.9f);
            shapeRenderer.rect(slotX, y, slotSize, slotSize);
        }
        shapeRenderer.end();

        // 2. Draw Slot Outlines with Selected Highlight
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < capacity; i++) {
            float slotX = x + i * (slotSize + slotSpacing);
            if (i == selected) {
                shapeRenderer.setColor(Color.GOLD);
                shapeRenderer.rect(slotX, y, slotSize, slotSize);
                shapeRenderer.rect(slotX - 1, y - 1, slotSize + 2, slotSize + 2);
            } else {
                shapeRenderer.setColor(0.4f, 0.4f, 0.45f, 1f);
                shapeRenderer.rect(slotX, y, slotSize, slotSize);
            }
        }
        shapeRenderer.end();

        // 3. Draw Text / Labels
        if (batch != null && font != null) {
            batch.begin();
            for (int i = 0; i < capacity; i++) {
                float slotX = x + i * (slotSize + slotSpacing);
                // Draw key number
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, String.valueOf(i + 1), slotX + 4, y + slotSize - 4);

                Item item = inventory.getItem(i);
                if (item != null) {
                    boolean breadCooldown = item instanceof Bread && ((Bread) item).isOnCooldown();
                    font.setColor(breadCooldown ? Color.ORANGE : Color.WHITE);
                    String name = item.getName();
                    if (name.length() > 6) {
                        name = name.substring(0, 5) + ".";
                    }
                    font.draw(batch, name, slotX + 4, y + 22);
                    if (breadCooldown) {
                        int secs = (int) Math.ceil(((Bread) item).getCooldownRemaining());
                        font.setColor(Color.YELLOW);
                        font.draw(batch, secs + "s", slotX + 26, y + slotSize - 4);
                    }
                }
            }

            // Draw selected item info above hotbar
            Item active = inventory.getSelected();
            if (active != null) {
                font.setColor(Color.GOLD);
                String statusSuffix = "";
                if (active instanceof Bread && ((Bread) active).isOnCooldown()) {
                    int secs = (int) Math.ceil(((Bread) active).getCooldownRemaining());
                    statusSuffix = " (Cooldown: " + secs + "s)";
                }
                font.draw(batch, "[" + (selected + 1) + "] " + active.getName() + statusSuffix + " - " + active.getDescription(), x, y + slotSize + 22);
            } else {
                font.setColor(Color.GRAY);
                font.draw(batch, "[" + (selected + 1) + "] Empty Slot", x, y + slotSize + 22);
            }
            batch.end();
        }
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getSlotSize() { return slotSize; }
    public void setSlotSize(float slotSize) { this.slotSize = slotSize; }
}

