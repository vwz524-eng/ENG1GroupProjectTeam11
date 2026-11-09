package io.github.gegame;

public class Student extends Sprite{

    private static final int MAX_HEALTH = 100;
    private static final double SPEED = 150; // pixels per second
    private static final int GOOSE_DAMAGE = 10;
    private static final double HIT_COOLDOWN = 1.0; //seconds of safety after goose attack
    private static final double UMBRELLA_SECONDS = 10.0;
 
    private int health = MAX_HEALTH;
 
    // Last direction moved, used for aiming the seed bag throw
    private double facingX = 1, facingY = 0; 
 
    private final Inventory inventory = new Inventory();
 
    private double umbrellaTimer = 0; // seconds of umbrella protection left once activated
    private double hitCooldown = 0;   // seconds until the goose can hurt us again
 

    public Student(double x, double y) {
        super(x, y, 32, 32);
        inventory.setItem(0, new SeedBag()); // key 1 on keyboard
        inventory.setItem(1, new Bread());   // key 2 on keyboard
        loadImage("images/player.png"); //Random path do change it!!
    }

    public void useSelectedItem(Goose goose) {
        Item item = inventory.getSelected();
        if (item != null && !item.isUsedUp()) {
            item.use(this, goose);
        }
    }

    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount);
    }

    public void giveUmbrella() {
        umbrellaTimer = UMBRELLA_SECONDS;
    }
 
    public boolean hasUmbrella() { return umbrellaTimer > 0; }

    public void checkGooseCollision(Goose goose) {
        if (collidesWith(goose)) {
            takeDamage(GOOSE_DAMAGE);
        }
    }
 
    public void takeDamage(int amount) {
        if (hasUmbrella() || hitCooldown > 0) return; // protected, nothing happens
        health = health - amount;
        hitCooldown = HIT_COOLDOWN; // so one touch doesn't hurt every frame
    }

    public void moveLeft(double dt)  { x -= SPEED * dt; facingX = -1; facingY = 0; }
    public void moveRight(double dt) { x += SPEED * dt; facingX = 1;  facingY = 0; }
    public void moveUp(double dt)    { y -= SPEED * dt; facingX = 0;  facingY = -1; }
    public void moveDown(double dt)  { y += SPEED * dt; facingX = 0;  facingY = 1; }

    //Getters and Setters
    public int getHealth() { return health; }
    public int getMaxHealth() { return MAX_HEALTH; }
    public double getHealthPercent() { return (double) health / MAX_HEALTH; } // for the health bar
    public void setHealth(int health) { this.health = Math.max(0, Math.min(MAX_HEALTH, health)); }
    public boolean isDead() { return health <= 0; }
 
    public double getUmbrellaTimeLeft() { return umbrellaTimer; }
    public double getSpeed() { return SPEED; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
 
    public Inventory getInventory() { return inventory; }
    public double getFacingX() { return facingX; }
    public double getFacingY() { return facingY; }



}
