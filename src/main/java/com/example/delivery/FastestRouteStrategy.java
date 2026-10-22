package com.example.delivery;

import java.util.List;
import java.util.Optional;

public class FastestRouteStrategy implements RouteStrategy {

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
        int bestTime = Integer.MAX_VALUE;
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

            int finalTime =
                    RouteRules.calculateFinalMinutes(route);

            if (finalTime < bestTime) {
                bestTime = finalTime;
                bestRoute = route;
            }
        }

        if (bestRoute == null) {
            throw new NoSuitableRouteException(
                    "No route satisfies the delivery constraints"
            );
        }

        String explanation =
                "Selected " + bestRoute.id()
                        + " because it has the lowest final time."
                        + System.lineSeparator()
                        + "Estimated time: "
                        + bestRoute.estimatedMinutes()
                        + " minutes"
                        + System.lineSeparator()
                        + "Traffic penalty: +"
                        + bestRoute.trafficLevel()
                        .getPenaltyMinutes()
                        + " minutes"
                        + System.lineSeparator()
                        + "Final time: "
                        + bestTime
                        + " minutes";

        if (rejectedRoutes.length() > 0) {
            explanation += System.lineSeparator()
                    + "Rejected routes:"
                    + System.lineSeparator()
                    + rejectedRoutes;
        }

        return new RouteDecision(
                bestRoute,
                getStrategyName(),
                bestTime,
                explanation
        );
    }

    @Override
    public String getStrategyName() {
        return "Fastest Route Strategy";
    }
}