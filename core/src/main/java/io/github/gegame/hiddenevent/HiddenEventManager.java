package io.github.gegame.hiddenevent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.gegame.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Manager that coordinates hidden event discovery, proximity triggers,
interaction execution, and tracks the hidden events counter (FR_HIDDEN_EVENTS)
 */

public class HiddenEventManager {
    private final List<HiddenEvent> events = new ArrayList<>();
    private final List<DiscoveryListener> listeners = new ArrayList<>();
    private int eventsInteracted = 0;

    private Texture statueTexture;
    private Texture treeTexture;
    private boolean texturesLoaded = false;

    public HiddenEventManager() {
    }

    //Registers a hidden event in manager

    public void registerEvent(HiddenEvent event) {
        if (event != null && !events.contains(event)) {
            events.add(event);
        }
    }

    /*
    Checks if player is within range of any hidden event.
    Marks the event as discovered and notifies listeners if newly found

    @param player the player to check
    @return the nearest active hidden event in trigger range, or null
     */

    public HiddenEvent checkProximity(Player player) {
        if (player == null) {
            return null;
        }

        double px = player.getX() + player.getWidth() / 2.0;
        double py = player.getY() + player.getHeight() / 2.0;

        for (HiddenEvent event : events) {
            if (!event.isCompleted() && event.checkTrigger(px, py)) {
                if (!event.isDiscovered()) {
                    event.discover();
                    notifyDiscovered(event);
                }
                return event;
            }
        }
        return null;
    }

    /*
    Triggers the nearby hidden event with player
    @param player the player interacting
    @return the resulting EventOutcome, or null if no event is within range
     */

    public EventOutcome interact(Player player) {
        HiddenEvent event = checkProximity(player);
        if (event != null && event.canActivate()) {
            EventOutcome outcome = event.activate(player, player.getInventory());
            eventsInteracted++;
            notifyActivated(event, outcome);
            return outcome;
        }
        return null;
    }

    /*
    Directly activates a speficic hidden event.
    @param event the event to activate
    @param player the player interacting
    @return the outcome produced
     */

    public EventOutcome activateEvent(HiddenEvent event, Player player) {
        if (event != null && event.canActivate() && player != null) {
            EventOutcome outcome = event.activate(player, player.getInventory());
            eventsInteracted++;
            notifyActivated(event, outcome);
            return outcome;
        }
        return null;
    }

    public void addListener(DiscoveryListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(DiscoveryListener listener) {
        listeners.remove(listener);
    }

    private void notifyDiscovered(HiddenEvent event) {
        for (DiscoveryListener l : listeners) {
            l.onEventDiscovered(event);
        }
    }

    private void notifyActivated(HiddenEvent event, EventOutcome outcome) {
        for (DiscoveryListener l : listeners) {
            l.onEventActivated(event, outcome);
        }
    }

    private void ensureTexturesLoaded() {
        if (texturesLoaded) return;
        texturesLoaded = true;
        if (Gdx.files != null) {
            if (Gdx.files.internal("maps/longboi_statue.png").exists()) {
                statueTexture = new Texture(Gdx.files.internal("maps/longboi_statue.png"));
            }
            if (Gdx.files.internal("maps/tree.png").exists()) {
                treeTexture = new Texture(Gdx.files.internal("maps/tree.png"));
            }
        }
    }

    /*
    Renders hidden event sprites using asset textures
    @param batch the SpriteBatch to draw with
     */

    public void draw(SpriteBatch batch) {
        if (batch == null) return;
        ensureTexturesLoaded();

        for (HiddenEvent event : events) {
            float ex = (float) event.getX();
            float ey = (float) event.getY();

            if ("STATUE".equalsIgnoreCase(event.getEventType()) && statueTexture != null) {
                float w = statueTexture.getWidth() * 0.5f;
                float h = statueTexture.getHeight() * 0.625f;
                batch.draw(statueTexture, ex - w / 2f, ey - h / 2f, w, h);
            } else if ("TREE".equalsIgnoreCase(event.getEventType()) && treeTexture != null) {
                float w = treeTexture.getWidth() * 0.5f;
                float h = treeTexture.getHeight() * 0.625f;
                batch.draw(treeTexture, ex - w / 2f, ey - h / 2f, w, h);
            }
        }
    }

    /*
    Fallback rendered when asset textures are unavailable
    @param shapeRendered the Shaperenderer to draw with
     */

    public void draw(ShapeRenderer shapeRenderer) {
        if (shapeRenderer == null) return;
        ensureTexturesLoaded();

        for (HiddenEvent event : events) {
            float ex = (float) event.getX();
            float ey = (float) event.getY();

            if ("STATUE".equalsIgnoreCase(event.getEventType())) {
                if (statueTexture != null) continue;
                // Long Boi Statue fallback
                shapeRenderer.setColor(0.85f, 0.70f, 0.20f, 0.85f);
                shapeRenderer.rect(ex - 16, ey - 20, 32, 40);
                shapeRenderer.setColor(1.0f, 0.9f, 0.4f, 1.0f);
                shapeRenderer.circle(ex, ey + 24, 12);
            } else if ("TREE".equalsIgnoreCase(event.getEventType())) {
                if (treeTexture != null) continue;
                // Tree fallback
                shapeRenderer.setColor(0.45f, 0.25f, 0.10f, 1.0f);
                shapeRenderer.rect(ex - 8, ey - 20, 16, 26);
                shapeRenderer.setColor(0.15f, 0.65f, 0.25f, 0.85f);
                shapeRenderer.circle(ex, ey + 18, 22);
            } else {
                // Generic secret marker
                shapeRenderer.setColor(Color.MAGENTA);
                shapeRenderer.circle(ex, ey, 14);
            }
        }
    }

    public void dispose() {
        if (statueTexture != null) {
            statueTexture.dispose();
            statueTexture = null;
        }
        if (treeTexture != null) {
            treeTexture.dispose();
            treeTexture = null;
        }
        texturesLoaded = false;
    }

    public List<HiddenEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public int getEventsInteracted() {
        return eventsInteracted;
    }

    public void setEventsInteracted(int count) {
        this.eventsInteracted = Math.max(0, count);
    }

    public void reset() {
        events.clear();
        eventsInteracted = 0;
    }
}

