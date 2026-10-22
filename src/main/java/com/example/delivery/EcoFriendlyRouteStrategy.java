package com.example.delivery;

import java.util.List;
import java.util.Optional;

public class EcoFriendlyRouteStrategy implements RouteStrategy {

    private final double emissionWeight;
    private final double distanceWeight;

    public EcoFriendlyRouteStrategy() {
        this(0.70, 0.30);
    }

    public EcoFriendlyRouteStrategy(
            double emissionWeight,
            double distanceWeight
    ) {
        if (emissionWeight < 0 || distanceWeight < 0) {
            throw new IllegalArgumentException(
                    "Eco weights cannot be negative"
            );
        }

        double totalWeight = emissionWeight + distanceWeight;

        if (Math.abs(totalWeight - 1.0) > 0.0001) {
            throw new IllegalArgumentException(
                    "Eco weights must equal 1.0"
            );
        }

        this.emissionWeight = emissionWeight;
        this.distanceWeight = distanceWeight;
    }

    @Override
    public RouteDecision selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        if (routes == null || routes.isEmpty()) {
            throw new NoSuitableRouteException(
                    "Route list is empty"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Delivery request is required"
            );
        }

        Route bestRoute = null;
        double lowestScore = Double.POSITIVE_INFINITY;
        StringBuilder rejectedRoutes = new StringBuilder();

        for (Route route : routes) {
            Optional<String> violation =
                    RouteRules.findViolation(route, request);

            if (violation.isPresent()) {
                rejectedRoutes
                        .append(route.id())
                        .append(" rejected: ")
                        .append(violation.get())
                        .append(System.lineSeparator());

                continue;
            }

            double ecoScore =
                    calculateEcoScore(route);

            if (ecoScore < lowestScore) {
                lowestScore = ecoScore;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException(
                    "No route satisfies the delivery constraints"
            );
        }

        String explanation = String.format(
                "Selected %s because it has the lowest eco score.%n"
                        + "Carbon emission: %.2f kg%n"
                        + "Emission weight: %.2f%n"
                        + "Distance: %.2f km%n"
                        + "Distance weight: %.2f%n"
                        + "Final eco score: %.2f",
                bestRoute.id(),
                bestRoute.carbonEmissionKg(),
                emissionWeight,
                bestRoute.distanceKm(),
                distanceWeight,
                lowestScore
        );

        if (rejectedRoutes.length() > 0) {
            explanation += System.lineSeparator()
                    + "Rejected routes:"
                    + System.lineSeparator()
                    + rejectedRoutes;
        }

        return new RouteDecision(
                bestRoute,
                getStrategyName(),
                lowestScore,
                explanation
        );
    }

    private double calculateEcoScore(Route route) {
        return route.carbonEmissionKg() * emissionWeight
                + route.distanceKm() * distanceWeight;
    }

    @Override
    public String getStrategyName() {
        return "Eco-Friendly Route Strategy";
    }
}