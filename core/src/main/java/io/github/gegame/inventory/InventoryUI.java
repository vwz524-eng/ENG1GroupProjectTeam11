
package io.github.gegame.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.gegame.hiddenevent.UmbrellaItem;
import io.github.gegame.hiddenevent.AllergyMedicationItem;

//Renders the inventory HUD on screen

public class InventoryUI {
    private final Inventory inventory;
    private final Texture seedIcon, breadIcon, umbrellaIcon, medicineIcon;
    private final float x = 150f;
    private final float y = 20f;

    public InventoryUI(Inventory inventory) {
        this.inventory = inventory;
        seedIcon = load("inventory/birdseed.png");
        breadIcon = load("inventory/breadcrumbs.png");
        umbrellaIcon = load("inventory/umbrella.png");
        medicineIcon = load("inventory/allergy_medication.png");
    }

    private Texture load(String path) {
        return Gdx.files.internal(path).exists() ? new Texture(Gdx.files.internal(path)) : null;
    }

    public void dispose() {
        for (Texture texture : new Texture[] {seedIcon, breadIcon, umbrellaIcon, medicineIcon}) {
            if (texture != null) texture.dispose();
        }
    }

    private Texture iconFor(Item item) {
        if (item == null) return null;
        return switch (item.getId()) {
            case "seed_bag" -> seedIcon;
            case "bread" -> breadIcon;
            case "umbrella" -> umbrellaIcon;
            case "allergy_medication" -> medicineIcon;
            default -> null;
        };
    }

    /**
     * Renders the hotbar slots and contents
     * @param shapeRenderer renderer for slot boxes
     * @param batch sprite batch for text rendering
     * @param font font for slot numbers and item names
     */

    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        if (inventory == null) return;

        int capacity = inventory.getCapacity();
        int selected = inventory.getSelectedIndex();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float slotSpacing = 8f;
        float slotSize = 52f;
        for (int i = 0; i < capacity; i++) {
            float slotX = x + i * (slotSize + slotSpacing);
            shapeRenderer.setColor(0.12f, 0.12f, 0.16f, 0.9f);
            shapeRenderer.rect(slotX, y, slotSize, slotSize);
        }
        shapeRenderer.end();

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

        if (batch != null && font != null) {
            batch.begin();
            for (int i = 0; i < capacity; i++) {
                float slotX = x + i * (slotSize + slotSpacing);

                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, String.valueOf(i + 1), slotX + 4, y + slotSize - 4);

                Item item = inventory.getItem(i);
                Texture icon = iconFor(item);
                if (icon != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(icon, slotX + 26, y + 24, 24, 24);
                }
                if (item != null) {
                    boolean breadCooldown = item instanceof Bread && ((Bread) item).isOnCooldown();
                    font.setColor(breadCooldown ? Color.ORANGE : Color.WHITE);
                    String name = item.getName();
                    if (name.length() > 6) {
                        name = name.substring(0, 5) + ".";
                    }
                    font.draw(batch, name, slotX + 3, y + 17);

                    String status = shortStatusFor(item);
                    font.setColor(item.isUsedUp() ? Color.GRAY : Color.YELLOW);
                    font.draw(batch, status, slotX + 3, y + 32);

                }
            }

            batch.end();
        }
    }


    private String shortStatusFor(Item item) {
        if (item instanceof Bread) {
            double remaining = ((Bread) item).getCooldownRemaining();
            return remaining > 0 ? formatSeconds(remaining) : "INF";
        }
        if (item instanceof SeedBag || "seed_bag".equals(item.getId())) {
            return "INF";
        }
        if (item instanceof UmbrellaItem) {
            return ((UmbrellaItem) item).getUsesRemaining() + "/3";
        }
        if (item instanceof AllergyMedicationItem || "allergy_medication".equals(item.getId())) {
            return Math.max(0, item.getQuantity()) + "/1";
        }
        return item.isUsedUp() ? "0" : String.valueOf(item.getQuantity());
    }

    private String formatSeconds(double seconds) {
        return String.format(java.util.Locale.ROOT, "%.1fs", seconds);
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
