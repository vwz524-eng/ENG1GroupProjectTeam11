package io.github.gegame.score;

public class DifficultyLevel {
    private String name;
    private float baseMultiplier;
    private TimerModifier timerModifier;
    private GooseSpeedModifier gooseSpeedModifier;
    private EventFrequencyModifier eventFrequencyModifier;

    public static final DifficultyLevel EASY = new DifficultyLevel(
        "Easy", 0.8f,
        new TimerModifier(1.2f),
        new GooseSpeedModifier(0.8f),
        new EventFrequencyModifier(1.2f)
    );

    public static final DifficultyLevel NORMAL = new DifficultyLevel(
        "Normal", 1.0f,
        new TimerModifier(1.0f),
        new GooseSpeedModifier(1.0f),
        new EventFrequencyModifier(1.0f)
    );

    public static final DifficultyLevel HARD = new DifficultyLevel(
        "Hard", 1.5f,
        new TimerModifier(0.8f),
        new GooseSpeedModifier(1.5f),
        new EventFrequencyModifier(0.8f)
    );

    public DifficultyLevel(String name, float baseMultiplier, TimerModifier timerModifier, 
                           GooseSpeedModifier gooseSpeedModifier, EventFrequencyModifier eventFrequencyModifier) {
        this.name = name != null ? name : "Normal";
        this.baseMultiplier = baseMultiplier > 0 ? baseMultiplier : 1.0f;
        this.timerModifier = timerModifier != null ? timerModifier : new TimerModifier();
        this.gooseSpeedModifier = gooseSpeedModifier != null ? gooseSpeedModifier : new GooseSpeedModifier();
        this.eventFrequencyModifier = eventFrequencyModifier != null ? eventFrequencyModifier : new EventFrequencyModifier();
    }

    public DifficultyLevel(String name, float baseMultiplier) {
        this(name, baseMultiplier, new TimerModifier(1.0f), new GooseSpeedModifier(baseMultiplier), new EventFrequencyModifier(1.0f));
    }

    public DifficultyLevel() {
        this("Normal", 1.0f);
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

    public void setBaseMultiplier(float baseMultiplier) {
        this.baseMultiplier = baseMultiplier > 0 ? baseMultiplier : 1.0f;
    }

    public TimerModifier getTimerModifier() {
        return timerModifier;
    }

    public void setTimerModifier(TimerModifier timerModifier) {
        this.timerModifier = timerModifier;
    }

    public GooseSpeedModifier getGooseSpeedModifier() {
        return gooseSpeedModifier;
    }

    public void setGooseSpeedModifier(GooseSpeedModifier gooseSpeedModifier) {
        this.gooseSpeedModifier = gooseSpeedModifier;
    }

    public EventFrequencyModifier getEventFrequencyModifier() {
        return eventFrequencyModifier;
    }

    public void setEventFrequencyModifier(EventFrequencyModifier eventFrequencyModifier) {
        this.eventFrequencyModifier = eventFrequencyModifier;
    }
}

