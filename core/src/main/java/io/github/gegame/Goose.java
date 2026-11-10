package io.github.gegame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
 
import java.util.ArrayList;
import java.util.List;

public class Goose extends Entity {

    public static final String ASSET = "goose.png";
    private enum State { CHASING, DISTRACTED, LEAVING };


    
    public static final int HUNGER_TO_FILL = 3; // pieces of bread to make it full
    private static final float SPEED = 90f;
    private static final float LEAVING_SPEED = 140f;

    private final Student player;
    private final float riverX, riverY;//Where it spawned so it can run back there.

    private State state = State.CHASING;
    private int hunger = 0;
 
    private float distractTimer = 0;
    private float targetX, targetY;
 
 
    public Goose(World world, float x, float y, Student player) {
        super(world, x, y, 40, 40);
        this.player = player;
        this.riverX = centerX();
        this.riverY = centerY();
    }

    @Override
    public Texture createTexture() {
        return new Texture(Gdx.files.internal(ASSET));
    }

      public void distract(float tx, float ty, float seconds) {
        if (state == State.LEAVING) return;
        state = State.DISTRACTED;
        targetX = tx;
        targetY = ty;
        distractTimer = seconds;
    }

    public void feed() {
        if (state == State.LEAVING) return;
        hunger++;
        if (hunger >= HUNGER_TO_FILL) {
            state = State.LEAVING;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float dt) {
        switch (state) {
            case CHASING:
                moveTowards(player.centerX(), player.centerY(), SPEED, dt);
                break;
 
            case DISTRACTED:
                distractTimer -= dt;
                if (distractTimer <= 0) {
                    state = State.CHASING;
                } else {
                    moveTowards(targetX, targetY, SPEED, dt);
                }
                break;
 
            case LEAVING:
                moveTowards(riverX, riverY, LEAVING_SPEED, dt);
                if (distanceTo(riverX, riverY) < 5f) {
                    destroy(); // it has run off the map
                    return;
                }
                break;
        }
 
        super.draw(batch, dt);
    }

      private void moveTowards(float tx, float ty, float speed, float dt) {
        float dx = tx - centerX();
        float dy = ty - centerY();
        float dist = (float) Math.hypot(dx, dy);
        if (dist > 1f) {
            setX(getX() + dx / dist * speed * dt);
            setY(getY() + dy / dist * speed * dt);
        }
    }
 
    //Geese currently in world.
    public static List<Goose> inWorld(World world) {
        List<Goose> geese = new ArrayList<Goose>();
        for (Entity e : new ArrayList<Entity>(world.getEntities())) {
            if (e instanceof Goose) {
                geese.add((Goose) e);
            }
        }
        return geese;
    }
 
    public boolean canHurt() { return state == State.CHASING; }
    public boolean isLeaving() { return state == State.LEAVING; }
    public boolean isDistracted() { return state == State.DISTRACTED; }
    public int getHunger() { return hunger; }
    public float getHungerPercent() { return (float) hunger / HUNGER_TO_FILL; }





}
