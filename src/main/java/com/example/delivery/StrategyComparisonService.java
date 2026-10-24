package com.example.delivery;

import java.util.ArrayList;
import java.util.List;

public class StrategyComparisonService {

    private final List<RouteStrategy> strategies;

    public StrategyComparisonService(
            List<RouteStrategy> strategies
    ) {
        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one strategy is required"
            );
        }

        for (RouteStrategy strategy : strategies) {
            if (strategy == null) {
                throw new IllegalArgumentException(
                        "Strategy list cannot contain null"
                );
            }
        }

        this.strategies = List.copyOf(strategies);
    }

    public List<RouteDecision> compare(
            List<Route> routes,
            DeliveryRequest request
    ) {
        List<RouteDecision> decisions = new ArrayList<>();

        RoutePlanner planner =
                new RoutePlanner(strategies.get(0));

        for (RouteStrategy strategy : strategies) {
            planner.setStrategy(strategy);

            RouteDecision decision =
                    planner.plan(routes, request);

            decisions.add(decision);
        }

        return List.copyOf(decisions);
    }
}