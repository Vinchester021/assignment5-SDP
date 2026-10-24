package com.example.delivery;

import java.util.List;
import java.util.Optional;

public class SafestRouteStrategy implements RouteStrategy {

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
        double highestSafetyScore = -1;
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

            boolean saferRoute =
                    route.safetyScore() > highestSafetyScore;

            boolean sameSafetyButFaster =
                    route.safetyScore() == highestSafetyScore
                            && bestRoute != null
                            && RouteRules.calculateFinalMinutes(route)
                            < RouteRules.calculateFinalMinutes(bestRoute);

            if (saferRoute || sameSafetyButFaster) {
                highestSafetyScore = route.safetyScore();
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException(
                    "No route satisfies the delivery constraints"
            );
        }

        String explanation = String.format(
                "Selected %s because it has the highest safety score.%n"
                        + "Safety score: %.2f out of 10%n"
                        + "Final delivery time: %d minutes%n"
                        + "The delivery constraints are satisfied.",
                bestRoute.id(),
                bestRoute.safetyScore(),
                RouteRules.calculateFinalMinutes(bestRoute)
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
                highestSafetyScore,
                explanation
        );
    }

    @Override
    public String getStrategyName() {
        return "Safest Route Strategy";
    }
}