package io.github.gegame.runlifecycle;

import com.badlogic.gdx.graphics.Texture;

/**
 * Manages colourblind mode options and preview as defined in the Game Class Diagram.
 */
public class Colour_blind_option {
    private Texture preview;
    private boolean cbmEnabled;

    public Colour_blind_option() {
        this.cbmEnabled = false;
    }

    public Colour_blind_option(Texture preview) {
        this.preview = preview;
        this.cbmEnabled = false;
    }

    public void enable_cbm() {
        this.cbmEnabled = true;
    }

    public void disable_cbm() {
        this.cbmEnabled = false;
    }

    public boolean isCbmEnabled() {
        return cbmEnabled;
    }

    public Texture getPreview() {
        return preview;
    }

    public void setPreview(Texture preview) {
        this.preview = preview;
    }
}
