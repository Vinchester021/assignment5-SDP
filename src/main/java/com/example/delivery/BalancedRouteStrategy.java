package com.example.delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BalancedRouteStrategy implements RouteStrategy {

    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy() {
        this(0.40, 0.30, 0.10, 0.20);
    }

    public BalancedRouteStrategy(
            double timeWeight,
            double costWeight,
            double distanceWeight,
            double emissionWeight
    ) {
        validateWeight(timeWeight);
        validateWeight(costWeight);
        validateWeight(distanceWeight);
        validateWeight(emissionWeight);

        double totalWeight =
                timeWeight
                        + costWeight
                        + distanceWeight
                        + emissionWeight;

        if (Math.abs(totalWeight - 1.0) > 0.0001) {
            throw new IllegalArgumentException(
                    "Balanced strategy weights must equal 1.0"
            );
        }

        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
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

        List<Route> eligibleRoutes = new ArrayList<>();
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
            } else {
                eligibleRoutes.add(route);
            }
        }

        if (eligibleRoutes.isEmpty()) {
            throw new NoSuitableRouteException(
                    "No route satisfies the delivery constraints"
            );
        }

        double maxTime = eligibleRoutes.stream()
                .mapToDouble(RouteRules::calculateFinalMinutes)
                .max()
                .orElse(1.0);

        double maxCost = eligibleRoutes.stream()
                .mapToDouble(route ->
                        RouteRules.calculateTotalCost(route, request))
                .max()
                .orElse(1.0);

        double maxDistance = eligibleRoutes.stream()
                .mapToDouble(Route::distanceKm)
                .max()
                .orElse(1.0);

        double maxEmission = eligibleRoutes.stream()
                .mapToDouble(Route::carbonEmissionKg)
                .max()
                .orElse(1.0);

        Route bestRoute = null;
        double lowestScore = Double.POSITIVE_INFINITY;

        for (Route route : eligibleRoutes) {
            double score = calculateBalancedScore(
                    route,
                    request,
                    maxTime,
                    maxCost,
                    maxDistance,
                    maxEmission
            );

            if (score < lowestScore) {
                lowestScore = score;
                bestRoute = route;
            }
        }

        double normalizedTime = normalize(
                RouteRules.calculateFinalMinutes(bestRoute),
                maxTime
        );

        double normalizedCost = normalize(
                RouteRules.calculateTotalCost(bestRoute, request),
                maxCost
        );

        double normalizedDistance = normalize(
                bestRoute.distanceKm(),
                maxDistance
        );

        double normalizedEmission = normalize(
                bestRoute.carbonEmissionKg(),
                maxEmission
        );

        String explanation = String.format(
                "Selected %s because it has the lowest balanced score.%n"
                        + "Normalized time: %.3f × %.2f%n"
                        + "Normalized cost: %.3f × %.2f%n"
                        + "Normalized distance: %.3f × %.2f%n"
                        + "Normalized emission: %.3f × %.2f%n"
                        + "Final balanced score: %.3f",
                bestRoute.id(),
                normalizedTime,
                timeWeight,
                normalizedCost,
                costWeight,
                normalizedDistance,
                distanceWeight,
                normalizedEmission,
                emissionWeight,
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

    private double calculateBalancedScore(
            Route route,
            DeliveryRequest request,
            double maxTime,
            double maxCost,
            double maxDistance,
            double maxEmission
    ) {
        double normalizedTime = normalize(
                RouteRules.calculateFinalMinutes(route),
                maxTime
        );

        double normalizedCost = normalize(
                RouteRules.calculateTotalCost(route, request),
                maxCost
        );

        double normalizedDistance = normalize(
                route.distanceKm(),
                maxDistance
        );

        double normalizedEmission = normalize(
                route.carbonEmissionKg(),
                maxEmission
        );

        return normalizedTime * timeWeight
                + normalizedCost * costWeight
                + normalizedDistance * distanceWeight
                + normalizedEmission * emissionWeight;
    }

    private double normalize(double value, double maximum) {
        if (maximum == 0) {
            return 0;
        }

        return value / maximum;
    }

    private void validateWeight(double weight) {
        if (!Double.isFinite(weight) || weight < 0) {
            throw new IllegalArgumentException(
                    "Weights must be finite and non-negative"
            );
        }
    }

    @Override
    public String getStrategyName() {
        return "Balanced Route Strategy";
    }
}