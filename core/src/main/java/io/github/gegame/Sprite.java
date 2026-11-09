package io.github.gegame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public abstract class Sprite {
    protected double x, y;
    protected int width, height;
    protected BufferedImage image;
 
    public Sprite(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void loadImage(String path) {
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception e) {
            // Prints the full path it looked in.
            System.out.println("Could not load image: " + new File(path).getAbsolutePath());
        }
    }
 
    public void draw(Graphics2D i) {
        if (image != null) {
            i.drawImage(image, (int) x, (int) y, width, height, null);
        } else {
            i.fillRect((int) x, (int) y, width, height); //If image isn't there will still draw something for testing.
        }
    }
 
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
 

    public boolean collidesWith(Sprite other) {//Collision detection
        return getBounds().intersects(other.getBounds());
    }
 
    public double centerX() { return x + width / 2.0; }
    public double centerY() { return y + height / 2.0; }
 
    public double distanceTo(Sprite other) {
        return Math.hypot(centerX() - other.centerX(), centerY() - other.centerY());
    }
 
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }


}
