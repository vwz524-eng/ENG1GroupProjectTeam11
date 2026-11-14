package io.github.gegame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Timer {
    private final Texture clockImage = new Texture("digital_clock_blank.png");
    private final Texture[] digits = new Texture[10];
    private final Texture colonDot;

    private float time = 0f;
    private boolean running = true;
    private final float x, y;

    public Timer(float x, float y) {
        this.x = x;
        this.y = y;
        for (int i = 0; i < 10; i++) {
            digits[i] = new Texture("digit_" + i + ".png");
        }
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.31f, 1f, 0.47f, 1f));
        pixmap.fillCircle(5, 5, 5);
        colonDot = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float delta) {
        if (running) {
            time = Math.min(time + delta, 5999f);   // caps at 99:59
        }
    }


    public void draw(SpriteBatch batch) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);

        batch.draw(clockImage, x, y, 320, 200);
        float dy = y + 62;
        batch.draw(digits[minutes / 10], x + 48,  dy, 40, 80);
        batch.draw(digits[minutes % 10], x + 98,  dy, 40, 80);
        batch.draw(digits[seconds / 10], x + 178, dy, 40, 80);
        batch.draw(digits[seconds % 10], x + 228, dy, 40, 80);

        if (seconds % 2 == 0 || !running) {
            batch.draw(colonDot, x + 148, y + 113, 10, 10);
            batch.draw(colonDot, x + 148, y + 81, 10, 10);
        }
    }

    public void pause()  { running = false; }
    public void resume() { running = true; }
    public void reset()  { time = 0f; }
    public float getTime() { return time; }

    public void dispose() {
        clockImage.dispose();
        colonDot.dispose();
        for (Texture t : digits) t.dispose();
    }
}