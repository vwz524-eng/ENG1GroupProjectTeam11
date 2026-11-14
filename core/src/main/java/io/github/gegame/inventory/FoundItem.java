package io.github.gegame.inventory;

import io.github.gegame.Player;
import io.github.gegame.Student;

/**
 * A collectible item found in the game world that can be collected and used.
 */
public class FoundItem extends Item {
    private boolean isCollected;

    public FoundItem() {
        this("found_item", "Found Item", "An item found in the world", false);
    }

    public FoundItem(String id, String name, String description) {
        this(id, name, description, false);
    }

    public FoundItem(String id, String name, String description, boolean isCollected) {
        super(id, name, description);
        this.isCollected = isCollected;
    }

    @Override
    public void use(Player player) {
        if (isCollected) {
            if ("umbrella".equalsIgnoreCase(getId()) && player instanceof Student) {
                ((Student) player).giveUmbrella();
            }
            consume();
        }
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        this.isCollected = collected;
    }
}

