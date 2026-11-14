package io.github.gegame.animation;


// The entity decides the state of the animation
public interface AnimState {
    // Filename for this state, or "" for the idle
    String fileToken();
}
