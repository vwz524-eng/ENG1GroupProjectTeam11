package io.github.gegame.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <summary>
 * Loads and plays directional sprite-sheet animations for one entity.
 * <p>
 * Each (state, direction) pair is one PNG whose frames sit side by side in a single row:
 * <pre>
 *   [frame0][frame1][frame2]...
 * </pre>
 * A plain single-image PNG is just a sheet with one frame, so static art works too.
 * <p>
 * Filenames are built as {@code folder/prefix[_token]_direction.png}, e.g.
 * {@code new SpriteAnimator("geese", "goose")} + state token "walk" + LEFT
 * -> {@code geese/goose_walk_left.png}.
 * <\summary>
 */
public class SpriteAnimator {
    /** Pass as frameCount to work it out from the image: assumes square frames (frame width == image height). */
    public static final int AUTO = 0;

    private final String folder;
    private final String prefix;

    private final Map<AnimState, EnumMap<Direction, Animation<TextureRegion>>> animations = new HashMap<>();
    private final List<Texture> textures = new ArrayList<>();

    private AnimState defaultState; // first registered state, used as a fallback
    private AnimState state;
    private Direction direction = Direction.DOWN;
    private float stateTime = 0f;

    public SpriteAnimator(String folder, String prefix) {
        this.folder = folder;
        this.prefix = prefix;
    }

    /**
     * Loads all four directions of a state. Chainable.
     * Missing files are skipped with a console message rather than crashing the game.
     *
     * @param frameCount    frames per sheet, or {@link #AUTO}
     * @param frameDuration seconds each frame is shown
     * @param looping       true to loop, false to play once and hold the last frame
     */
    public SpriteAnimator register(AnimState animState, int frameCount, float frameDuration, boolean looping) {
        if (Gdx.files == null) return this; // headless / unit-test environment

        EnumMap<Direction, Animation<TextureRegion>> perDirection = new EnumMap<>(Direction.class);
        for (Direction dir : Direction.values()) {
            Animation<TextureRegion> anim = load(pathFor(animState, dir), frameCount, frameDuration, looping);
            if (anim != null) perDirection.put(dir, anim);
        }

        if (!perDirection.isEmpty()) {
            animations.put(animState, perDirection);
            if (defaultState == null) defaultState = animState;
        }
        return this;
    }

    // The asset path, currently in /assets/
    public String pathFor(AnimState animState, Direction dir) {
        String token = animState.fileToken();
        return folder + "/" + prefix + (token.isEmpty() ? "" : "_" + token) + "_" + dir.suffix() + ".png";
    }

    private Animation<TextureRegion> load(String path, int frameCount, float frameDuration, boolean looping) {
        try {
            if (!Gdx.files.internal(path).exists()) {
                System.out.println("Missing texture!: " + path);
                return null;
            }

            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest); // crisp pixel art
            textures.add(texture);

            int frames = frameCount > 0 ? frameCount : Math.max(1, texture.getWidth() / texture.getHeight());
            int frameWidth = texture.getWidth() / frames;
            TextureRegion[][] grid = TextureRegion.split(texture, frameWidth, texture.getHeight());

            Array<TextureRegion> keyFrames = new Array<TextureRegion>(grid[0]); // first row only
            return new Animation<TextureRegion>(frameDuration, keyFrames,
                    looping ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        } catch (Exception e) {
            return null;
        }
    }

    // Animation resarts only when the state changes
    public void play(AnimState newState, Direction newDirection) {
        if (newState != state) {
            state = newState;
            stateTime = 0f;
        }
        direction = newDirection;
    }

    public void update(double dt) {
        stateTime += (float) dt;
    }

    // Current frame, null if nothing loads, caller falls back to shape
    public TextureRegion getFrame() {
        Animation<TextureRegion> anim = currentAnimation();
        return anim == null ? null : anim.getKeyFrame(stateTime);
    }

    /* True once a nonlooping animation - player throw - has played all frames */
    public boolean isFinished() {
        Animation<TextureRegion> anim = currentAnimation();
        return anim != null && anim.isAnimationFinished(stateTime);
    }

    public boolean hasFrames() {
        return !animations.isEmpty();
    }

    private Animation<TextureRegion> currentAnimation() {
        EnumMap<Direction, Animation<TextureRegion>> set = animations.get(state);
        Animation<TextureRegion> anim = set != null ? set.get(direction) : null;

        if (anim == null && defaultState != null) { // requested art missing -> fall back to idle
            set = animations.get(defaultState);
            anim = set != null ? set.get(direction) : null;
        }
        return anim;
    }

    public void dispose() {
        for (Texture texture : textures) texture.dispose();
        textures.clear();
        animations.clear();
    }
}
