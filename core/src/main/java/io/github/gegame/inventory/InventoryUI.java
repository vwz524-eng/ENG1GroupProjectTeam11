
package io.github.gegame.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class InventoryUI {

    private final Inventory inventory;

    private final Texture seedIcon;
    private final Texture breadIcon;
    private final Texture umbrellaIcon;
    private final Texture medicineIcon;

    private float slotSize = 52f;
    private float slotSpacing = 8f;
    private float x = 150f;
    private float y = 20f;

    public InventoryUI(Inventory inventory) {
        this.inventory = inventory;

        seedIcon = load("inventory/birdseed.png");
        breadIcon = load("inventory/breadcrumbs.png");
        umbrellaIcon = load("inventory/umbrella.png");
        medicineIcon = load("inventory/allergy_medication.png");
    }

    // Load an image if it exists
    private Texture load(String path) {
        if (Gdx.files.internal(path).exists()) {
            return new Texture(Gdx.files.internal(path));
        }
        Gdx.app.error("InventoryUI", "Missing image: " + path);
        return null;
    }

    // Match each item with its picture
    private Texture iconFor(Item item) {
        if (item == null) return null;

        switch (item.getId()) {
            case "seed_bag":
                return seedIcon;
            case "bread":
                return breadIcon;
            case "umbrella":
                return umbrellaIcon;
            case "allergy_medication":
                return medicineIcon;
            default:
                return null;
        }
    }

    public void render(ShapeRenderer shapeRenderer,
                       SpriteBatch batch,
                       BitmapFont font) {

        if (inventory == null) return;

        int capacity = inventory.getCapacity();
        int selected = inventory.getSelectedIndex();

        // Draw inventory slot backgrounds
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < capacity; i++) {
            float slotX = x + i * (slotSize + slotSpacing);

            shapeRenderer.setColor(0.12f, 0.12f, 0.16f, 0.9f);
            shapeRenderer.rect(slotX, y, slotSize, slotSize);
        }

        shapeRenderer.end();

        // Draw outlines and selected slot
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < capacity; i++) {
            float slotX = x + i * (slotSize + slotSpacing);

            if (i == selected) {
                shapeRenderer.setColor(Color.GOLD);
                shapeRenderer.rect(slotX, y, slotSize, slotSize);
                shapeRenderer.rect(
                    slotX - 1, y - 1,
                    slotSize + 2, slotSize + 2
                );
            } else {
                shapeRenderer.setColor(0.4f, 0.4f, 0.45f, 1f);
                shapeRenderer.rect(slotX, y, slotSize, slotSize);
            }
        }

        shapeRenderer.end();

        // Draw item pictures, names and slot numbers
        if (batch != null && font != null) {
            batch.begin();

            for (int i = 0; i < capacity; i++) {
                float slotX = x + i * (slotSize + slotSpacing);

                font.setColor(Color.LIGHT_GRAY);
                font.draw(
                    batch,
                    String.valueOf(i + 1),
                    slotX + 4,
                    y + slotSize - 4
                );

                Item item = inventory.getItem(i);

                if (item != null) {
                    Texture icon = iconFor(item);

                    if (icon != null) {
                        batch.setColor(Color.WHITE);
                        batch.draw(
                            icon,
                            slotX + 26,
                            y + 24,
                            24,
                            24
                        );
                    }

                    boolean breadCooldown =
                        item instanceof Bread
                            && ((Bread) item).isOnCooldown();

                    font.setColor(
                        breadCooldown ? Color.ORANGE : Color.WHITE
                    );

                    String name = item.getName();

                    if (name.length() > 6) {
                        name = name.substring(0, 5) + ".";
                    }

                    font.draw(batch, name, slotX + 3, y + 17);

                    if (item.isUsedUp()) {
                        font.setColor(Color.GRAY);
                        font.draw(batch, "0", slotX + 34, y + 34);
                    }

                    if (breadCooldown) {
                        int secs = (int) Math.ceil(
                            ((Bread) item).getCooldownRemaining()
                        );

                        font.setColor(Color.YELLOW);
                        font.draw(
                            batch,
                            secs + "s",
                            slotX + 26,
                            y + slotSize - 4
                        );
                    }
                }
            }

            // Selected item description above hotbar
            Item active = inventory.getSelected();

            if (active != null) {
                font.setColor(Color.GOLD);

                String statusSuffix = "";

                if (active instanceof Bread
                    && ((Bread) active).isOnCooldown()) {

                    int secs = (int) Math.ceil(
                        ((Bread) active).getCooldownRemaining()
                    );

                    statusSuffix = " (Cooldown: " + secs + "s)";
                }

                font.draw(
                    batch,
                    "[" + (selected + 1) + "] "
                        + active.getName()
                        + statusSuffix
                        + " - " + active.getDescription(),
                    x,
                    y + slotSize + 22
                );
            } else {
                font.setColor(Color.GRAY);
                font.draw(
                    batch,
                    "[" + (selected + 1) + "] Empty Slot",
                    x,
                    y + slotSize + 22
                );
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
    public void setSlotSize(float slotSize) {
        this.slotSize = slotSize;
    }

    // Free the images when no longer needed
    public void dispose() {
        if (seedIcon != null) seedIcon.dispose();
        if (breadIcon != null) breadIcon.dispose();
        if (umbrellaIcon != null) umbrellaIcon.dispose();
        if (medicineIcon != null) medicineIcon.dispose();
    }
}
