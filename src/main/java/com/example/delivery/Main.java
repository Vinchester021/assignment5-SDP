package com.example.delivery;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Route> routes = DemoData.createRoutes();

        DeliveryRequest request =
                DemoData.createStandardRequest();

        RoutePlanner planner =
                new RoutePlanner(
                        new FastestRouteStrategy()
                );

        System.out.println(
                "=== SMART DELIVERY ROUTE OPTIMIZATION ==="
        );

        System.out.println("\n--- 1. Fastest strategy ---");
        printDecision(planner.plan(routes, request));

        planner.setStrategy(
                new CheapestRouteStrategy()
        );

        System.out.println("\n--- 2. Cheapest strategy ---");
        printDecision(planner.plan(routes, request));

        planner.setStrategy(
                new EcoFriendlyRouteStrategy()
        );

        System.out.println("\n--- 3. Eco-friendly strategy ---");
        printDecision(planner.plan(routes, request));

        planner.setStrategy(
                new BalancedRouteStrategy(
                        0.40,
                        0.30,
                        0.10,
                        0.20
                )
        );

        System.out.println("\n--- 4. Balanced strategy ---");
        printDecision(planner.plan(routes, request));

        System.out.println(
                "\n=== AUTOMATIC STRATEGY SELECTION ==="
        );

        StrategySelector selector = new StrategySelector();

        List<DeliveryRequest> requests = List.of(
                DemoData.createUrgentRequest(),
                DemoData.createEconomyRequest(),
                DemoData.createEcoRequest(),
                DemoData.createStandardRequest()
        );

        for (DeliveryRequest currentRequest : requests) {
            RouteStrategy selectedStrategy =
                    selector.selectStrategy(currentRequest);

            planner.setStrategy(selectedStrategy);

            System.out.println(
                    "\nRequest: "
                            + currentRequest.requestId()
            );

            System.out.println(
                    "Priority: "
                            + currentRequest.priority()
            );

            printDecision(
                    planner.plan(routes, currentRequest)
            );
        }

        System.out.println(
                "\n=== FIFTH STRATEGY DEMONSTRATION ==="
        );

        planner.setStrategy(
                new SafestRouteStrategy()
        );

        printDecision(
                planner.plan(routes, request)
        );

    }

    private static void printDecision(
            RouteDecision decision
    ) {
        System.out.println(
                "Strategy: " + decision.strategyName()
        );

        System.out.println(
                "Selected route: "
                        + decision.selectedRoute().id()
        );

        System.out.printf(
                "Score: %.3f%n",
                decision.score()
        );

        System.out.println("Explanation:");
        System.out.println(decision.explanation());
    }



}