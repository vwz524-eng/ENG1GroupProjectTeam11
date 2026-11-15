package io.github.gegame.interaction;

//Represents the result produced by executing an interaction

public class InteractionOutcome {
    private boolean successful;
    private String message;
    private int pointsAwarded;

    public InteractionOutcome() {
        this(false, "", 0);
    }

    public InteractionOutcome(boolean successful, String message, int pointsAwarded) {
        this.successful = successful;
        this.message = message != null ? message : "";
        this.pointsAwarded = pointsAwarded;
    }

    public static InteractionOutcome success(String message, int pointsAwarded) {
        return new InteractionOutcome(true, message, pointsAwarded);
    }

    public static InteractionOutcome failure(String message) {
        return new InteractionOutcome(false, message, 0);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message : "";
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    @Override
    public String toString() {
        return "InteractionOutcome{" +
                "successful=" + successful +
                ", message='" + message + '\'' +
                ", pointsAwarded=" + pointsAwarded +
                '}';
    }
}

