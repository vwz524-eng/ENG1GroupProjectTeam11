package io.github.gegame;
 
import java.util.ArrayList;
 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
 
public class Student extends Entity {
 
    public static final String ASSET = "student.png";
 
    private static final int MAX_HEALTH = 100;
    private static final float SPEED = 150; // pixels per second
    private static final int GOOSE_DAMAGE = 10;
    private static final float HIT_COOLDOWN = 1f; //seconds of safety after goose attack
    private static final float UMBRELLA_SECONDS = 10f;
    private static final float MESSAGE_SECONDS = 3f;
 
    private int health = MAX_HEALTH;
 
    // Last direction moved, used for aiming the seed bag throw
    private float facingX = 1, facingY = 0; //Begin to face right
 
    private final Inventory inventory = new Inventory();
 
    private float umbrellaTimer = 0; // seconds of umbrella protection left once activated
    private float hitCooldown = 0;   // seconds until the goose can hurt us again
 
    private String message = "";
    private float messageTimer = 0;
 
    private boolean reachedGoal = false;
 
    public Student(World world, float x, float y) {
        super(world, x, y, 32, 32);
        inventory.setItem(0, new birdSeed()); // key 1 on keyboard
        inventory.setItem(1, new breadCrumbs());   // key 2 on keyboard
    }
 
    @Override
    public Texture createTexture() {
        return new Texture(Gdx.files.internal(ASSET));
    }
 
    public void moveLeft(float timeDelta)  { setX(getX() - SPEED * timeDelta); facingX = -1; facingY = 0; }
    public void moveRight(float timeDelta) { setX(getX() + SPEED * timeDelta); facingX = 1;  facingY = 0; }
    public void moveUp(float timeDelta)    { setY(getY() + SPEED * timeDelta); facingX = 0;  facingY = 1; }
    public void moveDown(float timeDelta)  { setY(getY() - SPEED * timeDelta); facingX = 0;  facingY = -1; }
 
    @Override
    public void draw(SpriteBatch batch, float timeDelta) {
        umbrellaTimer = Math.max(0, umbrellaTimer - timeDelta);
        hitCooldown = Math.max(0, hitCooldown - timeDelta);
        messageTimer = Math.max(0, messageTimer - timeDelta);
 
        checkGooseCollisions();
        checkTouchedObjects();
 
        super.draw(batch, timeDelta);
    }
 
    private void checkGooseCollisions() {
        for (Goose goose : Goose.inWorld(world)) {
            if (goose.canHurt() && collidesWith(goose)) {
                takeDamage(GOOSE_DAMAGE);
            }
        }
    }
 
    // Trees heal us, the statue gives the umbrella, the lecture hall is the goal.
    private void checkTouchedObjects() {
        for (Entity e : new ArrayList<Entity>(world.getEntities())) {
            if (e instanceof Tree) {
                if (((Tree) e).checkPlayer(this)) {
                    showMessage("Wow, you found a hidden item!");
                }
            } else if (e instanceof Statue) {
                if (((Statue) e).checkPlayer(this)) {
                    showMessage("You got an umbrella! The goose can't touch you for 10 seconds.");
                }
            } else if (e instanceof LectureHall) {
                if (collidesWith(e)) {
                    reachedGoal = true;
                }
            }
        }
    }
 
    public void takeDamage(int amount) {
        if (hasUmbrella() || hitCooldown > 0) return; // protected, nothing happens
        health = Math.max(0, health - amount);
        hitCooldown = HIT_COOLDOWN; // so one touch doesn't hurt every frame
    }
 
    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount);
    }
 
    public void giveUmbrella() {
        umbrellaTimer = UMBRELLA_SECONDS;
    }
 
    public boolean hasUmbrella() { return umbrellaTimer > 0; }
 
    public void selectSlot(int slot) {
        inventory.select(slot);
    }
 
    public void useSelectedItem() {
        Item item = inventory.getSelected();
        if (item != null && !item.isUsedUp()) {
            item.use(this);
        }
    }
 
    private void showMessage(String text) {
        message = text;
        messageTimer = MESSAGE_SECONDS;
    }
 
    public boolean hasMessage() { return messageTimer > 0; }
    public String getMessage() { return message; }
 
    public int getHealth() { return health; }
    public int getMaxHealth() { return MAX_HEALTH; }
    public float getHealthPercent() { return (float) health / MAX_HEALTH; } // for the health bar
    public void setHealth(int health) { this.health = Math.max(0, Math.min(MAX_HEALTH, health)); }
    public boolean isDead() { return health <= 0; }
 
    public boolean hasReachedGoal() { return reachedGoal; }
    public float getUmbrellaTimeLeft() { return umbrellaTimer; }
    public float getSpeed() { return SPEED; }
    public void setPosition(float x, float y) { setX(x); setY(y); }
 
    public Inventory getInventory() { return inventory; }
    public float getFacingX() { return facingX; }
    public float getFacingY() { return facingY; }

}
