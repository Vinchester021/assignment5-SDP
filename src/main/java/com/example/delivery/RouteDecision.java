package com.example.delivery;

public record RouteDecision(
        Route selectedRoute,
        String strategyName,
        double score,
        String explanation
) {
    public RouteDecision {
        if (selectedRoute == null) {
            throw new IllegalArgumentException(
                    "Selected route cannot be null"
            );
        }

        if (strategyName == null || strategyName.isBlank()) {
            throw new IllegalArgumentException(
                    "Strategy name cannot be empty"
            );
        }

        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException(
                    "Explanation cannot be empty"
            );
        }
    }
}