package io.github.gegame;

public class TimeExpiredState {

    public void enter() {
        System.out.println("Time is up! You failed to escape.");
    }

    public void exit() {
        System.out.println("Leaving time expired state.");
    }
}
