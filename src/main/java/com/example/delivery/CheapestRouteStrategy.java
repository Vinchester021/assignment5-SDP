package com.example.delivery;

import java.util.List;
import java.util.Optional;

public class CheapestRouteStrategy implements RouteStrategy {

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
        double lowestCost = Double.POSITIVE_INFINITY;
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

            double totalCost =
                    RouteRules.calculateTotalCost(route, request);

            if (totalCost < lowestCost) {
                lowestCost = totalCost;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException(
                    "No route satisfies the delivery constraints"
            );
        }

        String explanation = String.format(
                "Selected %s because it has the lowest total cost.%n"
                        + "Base cost: %.2f%n"
                        + "Distance cost: %.2f%n"
                        + "Priority surcharge: %.2f%n"
                        + "Additional fees: %.2f%n"
                        + "Total cost: %.2f",
                bestRoute.id(),
                bestRoute.baseCost(),
                RouteRules.calculateDistanceCost(bestRoute),
                RouteRules.calculatePrioritySurcharge(request),
                bestRoute.additionalFees(),
                lowestCost
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
                lowestCost,
                explanation
        );
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Route Strategy";
    }
}