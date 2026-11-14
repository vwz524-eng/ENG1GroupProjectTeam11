package io.github.gegame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CampusMap extends ApplicationAdapter {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private FitViewport viewport;

    private float worldWidth;
    private float worldHeight;

    @Override
    public void create() {

        // Load the map created in Tiled
        map = new TmxMapLoader().load("maps/campus.tmx");

        int tilesWide =
            map.getProperties().get("width", Integer.class);

        int tilesHigh =
            map.getProperties().get("height", Integer.class);

        int tileWidth =
            map.getProperties().get("tilewidth", Integer.class);

        int tileHeight =
            map.getProperties().get("tileheight", Integer.class);

        worldWidth = tilesWide * tileWidth;
        worldHeight = tilesHigh * tileHeight;

        // Set up the camera
        camera = new OrthographicCamera();

        viewport = new FitViewport(
            worldWidth,
            worldHeight,
            camera
        );

        viewport.update(
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight(),
            true
        );

        // Create the map renderer
        renderer = new OrthogonalTiledMapRenderer(map);

        Gdx.app.log("CampusMap", "Campus loaded successfully");

        // Test that Java can find the starting position
        Vector2 spawn = getMarkerPosition("PlayerSpawn");

        if (spawn != null) {
            Gdx.app.log("CampusMap", "Player spawn: " + spawn);
        } else {
            Gdx.app.error("CampusMap", "PlayerSpawn not found");
        }

        // Check that the collision layer exists
        if (map.getLayers().get("Collision") == null) {
            Gdx.app.error("CampusMap", "Collision layer not found");
        }
    }

    @Override
    public void render() {

        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Display the campus
        viewport.apply();
        camera.update();

        renderer.setView(camera);
        renderer.render();
    }

    // Find a named object in the Triggers layer
    public Vector2 getMarkerPosition(String markerName) {

        MapLayer triggers = map.getLayers().get("Triggers");

        if (triggers == null) {
            return null;
        }

        for (MapObject object : triggers.getObjects()) {

            if (!markerName.equals(object.getName())) {
                continue;
            }

            if (object instanceof PointMapObject) {
                return new Vector2(
                    ((PointMapObject) object).getPoint()
                );
            }

            if (object instanceof RectangleMapObject) {

                Rectangle r =
                    ((RectangleMapObject) object).getRectangle();

                return new Vector2(
                    r.x + r.width / 2f,
                    r.y + r.height / 2f
                );
            }
        }

        return null;
    }

    // Check whether the player can move to a position
    public boolean canMoveTo(Rectangle playerBounds) {

        // Check map boundaries
        if (playerBounds.x < 0 ||
            playerBounds.y < 0 ||
            playerBounds.x + playerBounds.width > worldWidth ||
            playerBounds.y + playerBounds.height > worldHeight) {

            return false;
        }

        MapLayer collisionLayer =
            map.getLayers().get("Collision");

        if (collisionLayer == null) {
            Gdx.app.error("CampusMap", "Collision layer missing");
            return false;
        }

        // Check obstacles from Tiled
        for (MapObject object : collisionLayer.getObjects()) {

            if (object instanceof RectangleMapObject) {

                Rectangle obstacle =
                    ((RectangleMapObject) object).getRectangle();

                if (playerBounds.overlaps(obstacle)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }

    @Override
    public void dispose() {

        if (renderer != null) {
            renderer.dispose();
        }

        if (map != null) {
            map.dispose();
        }
    }
}
