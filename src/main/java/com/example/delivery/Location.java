package com.example.delivery;

public record Location(
        String name,
        double latitude,
        double longitude
) {
    public Location {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Location name cannot be empty");
        }

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }
    }
}