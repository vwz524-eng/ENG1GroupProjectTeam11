package io.github.gegame;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public abstract class Entity {
    protected Texture texture;
    protected Rectangle rectangle;
    protected World world;
    protected float angle = 0.0f;

 
    public Entity(World world) {
        this.world = world;
        world.getEntities().add(this);
    }

    public Entity(World world, float x, float y, float width, float height) {
        this(world);
        rectangle = new Rectangle(x, y, width, height);
    }

    public final Texture getTexture() {
        if (texture == null) {
            texture = createTexture();
        }
        return texture;
    }

    public abstract Texture createTexture();

    public Rectangle getRectangle() {
        return rectangle;
    }
 
    public float getAngle() {
        return angle;
    }
 
    public void setAngle(float angle) {
        this.angle = angle;
    }
 
    public float getX() {
        return rectangle.getX();
    }
 
    public float getY() {
        return rectangle.getY();
    }
 
    protected void setX(float x) {
        rectangle.x = x;
    }
 
    protected void setY(float y) {
        rectangle.y = y;
    }
 
    public float getWidth() {
        return rectangle.width;
    }
 
    public float getHeight() {
        return rectangle.height;
    }

    
    public void draw(SpriteBatch batch, float timeDelta) {

  
        if (batch != null) {
            batch.draw(getTexture(), getX(), getY(),
                getWidth() / 2, getWidth() / 2,
                getWidth(), getHeight(),
                1.0f, 1.0f, getAngle(),
                0, 0,
                (int) getWidth(), (int) getHeight(),
                false, false);
        }
    }

    public void damage() {}

    public void destroy() {
        world.getEntities().remove(this);
    }

     
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }

 
    public World getWorld() { return world; }

 

    public boolean collidesWith(Entity other) {
        return rectangle.overlaps(other.getRectangle());
    }
 
   
    public float centerX() { return getX() + getWidth() / 2f; }
    public float centerY() { return getY() + getHeight() / 2f; }
 
    public float distanceTo(float px, float py) {
        return (float) Math.hypot(centerX() - px, centerY() - py);
    }
 
    public float distanceTo(Entity other) {
        return distanceTo(other.centerX(), other.centerY());
    }


}
