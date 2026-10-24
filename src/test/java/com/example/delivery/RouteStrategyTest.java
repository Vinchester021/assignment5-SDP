package com.example.delivery;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteStrategyTest {

    private static final Location ORIGIN =
            new Location("Origin", 51.10, 71.40);

    private static final Location DESTINATION =
            new Location("Destination", 51.20, 71.50);

    @Test
    void fastestStrategySelectsRouteB() {
        RouteDecision decision =
                new FastestRouteStrategy().selectRoute(
                        DemoData.createRoutes(),
                        DemoData.createStandardRequest()
                );

        assertEquals(
                "Route B",
                decision.selectedRoute().id()
        );
    }

    @Test
    void fastestStrategyConsidersTrafficPenalty() {
        Route routeWithTraffic = createRoute(
                "TRAFFIC",
                5,
                10,
                500,
                1,
                TrafficLevel.HIGH,
                true,
                8
        );

        Route routeWithoutTraffic = createRoute(
                "CLEAR",
                7,
                16,
                500,
                1,
                TrafficLevel.LOW,
                true,
                8
        );

        RouteDecision decision =
                new FastestRouteStrategy().selectRoute(
                        List.of(
                                routeWithTraffic,
                                routeWithoutTraffic
                        ),
                        flexibleRequest()
                );

        assertEquals(
                "CLEAR",
                decision.selectedRoute().id()
        );
    }

    @Test
    void cheapestStrategySelectsRouteC() {
        RouteDecision decision =
                new CheapestRouteStrategy().selectRoute(
                        DemoData.createRoutes(),
                        DemoData.createStandardRequest()
                );

        assertEquals(
                "Route C",
                decision.selectedRoute().id()
        );
    }

    @Test
    void ecoStrategySelectsRouteH() {
        RouteDecision decision =
                new EcoFriendlyRouteStrategy().selectRoute(
                        DemoData.createRoutes(),
                        DemoData.createStandardRequest()
                );

        assertEquals(
                "Route H",
                decision.selectedRoute().id()
        );
    }

    @Test
    void balancedStrategySelectsRouteC() {
        RouteDecision decision =
                new BalancedRouteStrategy().selectRoute(
                        DemoData.createRoutes(),
                        DemoData.createStandardRequest()
                );

        assertEquals(
                "Route C",
                decision.selectedRoute().id()
        );
    }

    @Test
    void safestStrategySelectsRouteH() {
        RouteDecision decision =
                new SafestRouteStrategy().selectRoute(
                        DemoData.createRoutes(),
                        DemoData.createStandardRequest()
                );

        assertEquals(
                "Route H",
                decision.selectedRoute().id()
        );
    }

    @Test
    void routePlannerSupportsRuntimeSwitching() {
        RoutePlanner planner =
                new RoutePlanner(
                        new FastestRouteStrategy()
                );

        RouteDecision fastestDecision = planner.plan(
                DemoData.createRoutes(),
                DemoData.createStandardRequest()
        );

        planner.setStrategy(
                new EcoFriendlyRouteStrategy()
        );

        RouteDecision ecoDecision = planner.plan(
                DemoData.createRoutes(),
                DemoData.createStandardRequest()
        );

        assertEquals(
                "Route B",
                fastestDecision.selectedRoute().id()
        );

        assertEquals(
                "Route H",
                ecoDecision.selectedRoute().id()
        );

        assertNotEquals(
                fastestDecision.selectedRoute(),
                ecoDecision.selectedRoute()
        );
    }

    @Test
    void selectorChoosesFastestForUrgentRequest() {
        StrategySelector selector =
                new StrategySelector();

        RouteStrategy strategy =
                selector.selectStrategy(
                        DemoData.createUrgentRequest()
                );

        assertInstanceOf(
                FastestRouteStrategy.class,
                strategy
        );
    }

    @Test
    void selectorChoosesCheapestForEconomyRequest() {
        StrategySelector selector =
                new StrategySelector();

        RouteStrategy strategy =
                selector.selectStrategy(
                        DemoData.createEconomyRequest()
                );

        assertInstanceOf(
                CheapestRouteStrategy.class,
                strategy
        );
    }

    @Test
    void selectorChoosesEcoForEcoRequest() {
        StrategySelector selector =
                new StrategySelector();

        RouteStrategy strategy =
                selector.selectStrategy(
                        DemoData.createEcoRequest()
                );

        assertInstanceOf(
                EcoFriendlyRouteStrategy.class,
                strategy
        );
    }

    @Test
    void selectorChoosesBalancedForStandardRequest() {
        StrategySelector selector =
                new StrategySelector();

        RouteStrategy strategy =
                selector.selectStrategy(
                        DemoData.createStandardRequest()
                );

        assertInstanceOf(
                BalancedRouteStrategy.class,
                strategy
        );
    }

    @Test
    void unavailableRouteIsRejected() {
        Route unavailableRoute = createRoute(
                "UNAVAILABLE",
                2,
                5,
                200,
                0.2,
                TrafficLevel.LOW,
                false,
                10
        );

        Route availableRoute = createRoute(
                "AVAILABLE",
                10,
                20,
                500,
                1,
                TrafficLevel.LOW,
                true,
                7
        );

        RouteDecision decision =
                new FastestRouteStrategy().selectRoute(
                        List.of(
                                unavailableRoute,
                                availableRoute
                        ),
                        flexibleRequest()
                );

        assertEquals(
                "AVAILABLE",
                decision.selectedRoute().id()
        );

        assertTrue(
                decision.explanation()
                        .contains("Route is unavailable")
        );
    }

    @Test
    void routeExceedingBudgetIsRejected() {
        Route affordableRoute = createRoute(
                "AFFORDABLE",
                1,
                15,
                500,
                1,
                TrafficLevel.LOW,
                true,
                7
        );

        Route expensiveRoute = createRoute(
                "EXPENSIVE",
                1,
                10,
                700,
                1,
                TrafficLevel.LOW,
                true,
                9
        );

        DeliveryRequest request = new DeliveryRequest(
                "BUDGET-TEST",
                DeliveryPriority.ECONOMY,
                600,
                60,
                false,
                10
        );

        RouteDecision decision =
                new CheapestRouteStrategy().selectRoute(
                        List.of(
                                expensiveRoute,
                                affordableRoute
                        ),
                        request
                );

        assertEquals(
                "AFFORDABLE",
                decision.selectedRoute().id()
        );
    }

    @Test
    void routeExceedingDeadlineIsRejected() {
        Route fastRoute = createRoute(
                "FAST",
                5,
                10,
                500,
                1,
                TrafficLevel.LOW,
                true,
                7
        );

        Route slowRoute = createRoute(
                "SLOW",
                5,
                20,
                500,
                1,
                TrafficLevel.LOW,
                true,
                9
        );

        DeliveryRequest request = new DeliveryRequest(
                "DEADLINE-TEST",
                DeliveryPriority.STANDARD,
                10_000,
                15,
                false,
                10
        );

        RouteDecision decision =
                new FastestRouteStrategy().selectRoute(
                        List.of(slowRoute, fastRoute),
                        request
                );

        assertEquals(
                "FAST",
                decision.selectedRoute().id()
        );
    }

    @Test
    void noSuitableRouteThrowsException() {
        Route unavailableRoute = createRoute(
                "UNAVAILABLE",
                2,
                5,
                200,
                0.2,
                TrafficLevel.LOW,
                false,
                10
        );

        assertThrows(
                NoSuitableRouteException.class,
                () -> new FastestRouteStrategy()
                        .selectRoute(
                                List.of(unavailableRoute),
                                flexibleRequest()
                        )
        );
    }

    @Test
    void emptyRouteListThrowsException() {
        assertThrows(
                NoSuitableRouteException.class,
                () -> new CheapestRouteStrategy()
                        .selectRoute(
                                List.of(),
                                flexibleRequest()
                        )
        );
    }

    @Test
    void invalidBalancedWeightsAreRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(
                        0.50,
                        0.50,
                        0.50,
                        0.50
                )
        );
    }

    @Test
    void comparisonModeExecutesFiveStrategies() {
        StrategyComparisonService service =
                new StrategyComparisonService(
                        List.of(
                                new FastestRouteStrategy(),
                                new CheapestRouteStrategy(),
                                new EcoFriendlyRouteStrategy(),
                                new BalancedRouteStrategy(),
                                new SafestRouteStrategy()
                        )
                );

        List<RouteDecision> decisions = service.compare(
                DemoData.createRoutes(),
                DemoData.createStandardRequest()
        );

        assertEquals(5, decisions.size());
    }

    @Test
    void routeExactlyOnConstraintBoundaryIsAccepted() {
        Route boundaryRoute = new Route(
                "BOUNDARY",
                ORIGIN,
                DESTINATION,
                1,
                20,
                1000,
                100,
                1,
                TrafficLevel.MEDIUM,
                true,
                8
        );

        DeliveryRequest request = new DeliveryRequest(
                "BOUNDARY-TEST",
                DeliveryPriority.STANDARD,
                1450,
                27,
                false,
                1
        );

        RouteDecision decision =
                new CheapestRouteStrategy().selectRoute(
                        List.of(boundaryRoute),
                        request
                );

        assertEquals(
                "BOUNDARY",
                decision.selectedRoute().id()
        );
    }

    @Test
    void negativeRouteDistanceIsRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Route(
                        "INVALID",
                        ORIGIN,
                        DESTINATION,
                        -1,
                        10,
                        500,
                        0,
                        1,
                        TrafficLevel.LOW,
                        true,
                        8
                )
        );
    }

    private DeliveryRequest flexibleRequest() {
        return new DeliveryRequest(
                "FLEXIBLE-TEST",
                DeliveryPriority.STANDARD,
                100_000,
                1000,
                false,
                100
        );
    }

    private Route createRoute(
            String id,
            double distance,
            int minutes,
            double baseCost,
            double emission,
            TrafficLevel trafficLevel,
            boolean available,
            double safetyScore
    ) {
        return new Route(
                id,
                ORIGIN,
                DESTINATION,
                distance,
                minutes,
                baseCost,
                0,
                emission,
                trafficLevel,
                available,
                safetyScore
        );
    }
}