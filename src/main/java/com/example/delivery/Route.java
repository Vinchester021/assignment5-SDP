package com.example.delivery;

public record Route(
        String id,
        Location origin,
        Location destination,
        double distanceKm,
        int estimatedMinutes,
        double baseCost,
        double additionalFees,
        double carbonEmissionKg,
        TrafficLevel trafficLevel,
        boolean available,
        double safetyScore
) {
    public Route {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Route ID cannot be empty");
        }

        if (origin == null || destination == null) {
            throw new IllegalArgumentException(
                    "Origin and destination are required"
            );
        }

        if (distanceKm < 0
                || estimatedMinutes < 0
                || baseCost < 0
                || additionalFees < 0
                || carbonEmissionKg < 0) {
            throw new IllegalArgumentException(
                    "Route values cannot be negative"
            );
        }

        if (trafficLevel == null) {
            throw new IllegalArgumentException(
                    "Traffic level is required"
            );
        }

        if (safetyScore < 0 || safetyScore > 10) {
            throw new IllegalArgumentException(
                    "Safety score must be between 0 and 10"
            );
        }
    }
}