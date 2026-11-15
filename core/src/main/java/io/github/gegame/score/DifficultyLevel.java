package io.github.gegame.score;

public class DifficultyLevel {
    private String name;
    private final float baseMultiplier;
    private final GooseSpeedModifier gooseSpeedModifier;

    public static final DifficultyLevel EASY = new DifficultyLevel(
        "Easy", 0.8f,
        new GooseSpeedModifier(0.8f)
    );

    public static final DifficultyLevel NORMAL = new DifficultyLevel(
        "Normal", 1.0f,
        new GooseSpeedModifier(1.0f)
    );

    public static final DifficultyLevel HARD = new DifficultyLevel(
        "Hard", 1.5f,
        new GooseSpeedModifier(1.5f)
    );

    public DifficultyLevel(String name, float baseMultiplier,
                           GooseSpeedModifier gooseSpeedModifier) {
        this.name = name != null ? name : "Normal";
        this.baseMultiplier = baseMultiplier > 0 ? baseMultiplier : 1.0f;
        this.gooseSpeedModifier = gooseSpeedModifier != null ? gooseSpeedModifier : new GooseSpeedModifier();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBaseMultiplier() {
        return baseMultiplier;
    }

    public GooseSpeedModifier getGooseSpeedModifier() {
        return gooseSpeedModifier;
    }

}

