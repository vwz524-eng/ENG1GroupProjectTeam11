package io.github.gegame.interaction;

//Represents the result produced by executing an interaction

public record InteractionOutcome(boolean successful, String message, int pointsAwarded) {
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

    @Override
    public String toString() {
        return "InteractionOutcome{" +
            "successful=" + successful +
            ", message='" + message + '\'' +
            ", pointsAwarded=" + pointsAwarded +
            '}';
    }
}

