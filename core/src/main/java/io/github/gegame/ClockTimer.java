package io.github.gegame;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ClockTimer {
    private final Texture clockImage = new Texture("digital_clock_blank.png");
    private final Texture[] digits = new Texture[10];
    private final Texture colonDot;
    private final float x, y, scale;

    public ClockTimer(float x, float y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        for (int i = 0; i < 10; i++) {
            digits[i] = new Texture("digit_" + i + ".png");
        }
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.31f, 1f, 0.47f, 1f));
        pixmap.fillCircle(5, 5, 5);
        colonDot = new Texture(pixmap);
        pixmap.dispose();
    }

    // Call this between batch.begin() and batch.end()
    public void drawTime(SpriteBatch batch, int totalSeconds, boolean running) {
        totalSeconds = Math.max(0, Math.min(totalSeconds, 5999));
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        batch.draw(clockImage, x, y, 320 * scale, 200 * scale);
        float dy = y + 62 * scale;
        float w = 40 * scale, h = 80 * scale;
        
        batch.draw(digits[minutes / 10], x + 48 * scale,  dy, w, h);
        batch.draw(digits[minutes % 10], x + 98 * scale,  dy, w, h);
        batch.draw(digits[seconds / 10], x + 178 * scale, dy, w, h);
        batch.draw(digits[seconds % 10], x + 228 * scale, dy, w, h);

        if (seconds % 2 == 0 || !running) {
            float d = 10 * scale;
            batch.draw(colonDot, x + 148 * scale, y + 113 * scale, d, d);
            batch.draw(colonDot, x + 148 * scale, y + 81 * scale, d, d);
        }
    }

    public void dispose() {
        clockImage.dispose();
        colonDot.dispose();
        for (Texture t : digits) t.dispose();
    }
}
