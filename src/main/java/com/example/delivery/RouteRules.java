package com.example.delivery;

import java.util.Optional;

public final class RouteRules {

    private static final double DISTANCE_COST_PER_KM = 50.0;

    private RouteRules() {
    }

    public static int calculateFinalMinutes(Route route) {
        return route.estimatedMinutes()
                + route.trafficLevel().getPenaltyMinutes();
    }

    public static double calculateDistanceCost(Route route) {
        return route.distanceKm() * DISTANCE_COST_PER_KM;
    }

    public static double calculatePrioritySurcharge(
            DeliveryRequest request
    ) {
        return switch (request.priority()) {
            case URGENT -> 1000.0;
            case STANDARD -> 300.0;
            case ECO -> 200.0;
            case ECONOMY -> 0.0;
        };
    }

    public static double calculateTotalCost(
            Route route,
            DeliveryRequest request
    ) {
        return route.baseCost()
                + calculateDistanceCost(route)
                + calculatePrioritySurcharge(request)
                + route.additionalFees();
    }

    public static Optional<String> findViolation(
            Route route,
            DeliveryRequest request
    ) {
        if (!route.available()) {
            return Optional.of("Route is unavailable");
        }

        double totalCost = calculateTotalCost(route, request);

        if (totalCost > request.maxBudget()) {
            return Optional.of("Maximum budget exceeded");
        }

        int finalMinutes = calculateFinalMinutes(route);

        if (finalMinutes > request.maxDeliveryMinutes()) {
            return Optional.of("Delivery deadline exceeded");
        }

        if (route.carbonEmissionKg()
                > request.maxCarbonEmissionKg()) {
            return Optional.of("Emission limit exceeded");
        }

        return Optional.empty();
    }
}