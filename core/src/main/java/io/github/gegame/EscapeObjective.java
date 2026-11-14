package io.github.gegame;

public class EscapeObjective {

    private boolean required = true;
    private boolean completed = false;

    public void complete() {
        completed = true;
    }

    public boolean isSatisfied() {
        return required && completed;
    }
}

