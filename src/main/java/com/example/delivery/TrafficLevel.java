package com.example.delivery;

public enum TrafficLevel {

    LOW(0),
    MEDIUM(7),
    HIGH(15);

    private final int penaltyMinutes;

    TrafficLevel(int penaltyMinutes) {
        this.penaltyMinutes = penaltyMinutes;
    }

    public int getPenaltyMinutes() {
        return penaltyMinutes;
    }
}