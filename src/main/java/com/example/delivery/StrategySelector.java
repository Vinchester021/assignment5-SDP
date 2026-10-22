package com.example.delivery;

import java.util.EnumMap;
import java.util.Map;

public class StrategySelector {

    private final Map<DeliveryPriority, RouteStrategy> strategies =
            new EnumMap<>(DeliveryPriority.class);

    public StrategySelector() {
        registerStrategy(
                DeliveryPriority.URGENT,
                new FastestRouteStrategy()
        );

        registerStrategy(
                DeliveryPriority.ECONOMY,
                new CheapestRouteStrategy()
        );

        registerStrategy(
                DeliveryPriority.ECO,
                new EcoFriendlyRouteStrategy()
        );

        registerStrategy(
                DeliveryPriority.STANDARD,
                new BalancedRouteStrategy()
        );
    }

    public void registerStrategy(
            DeliveryPriority priority,
            RouteStrategy strategy
    ) {
        if (priority == null || strategy == null) {
            throw new IllegalArgumentException(
                    "Priority and strategy are required"
            );
        }

        strategies.put(priority, strategy);
    }

    public RouteStrategy selectStrategy(
            DeliveryRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Delivery request is required"
            );
        }

        RouteStrategy strategy =
                strategies.get(request.priority());

        if (strategy == null) {
            throw new IllegalStateException(
                    "No strategy registered for priority: "
                            + request.priority()
            );
        }

        return strategy;
    }
}