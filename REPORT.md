# Assignment 5 Report

## Smart Delivery and Route Optimization Engine

**Student:** Vilgelm Feller  
**Pattern:** Strategy  
**Language:** Java 17  
**Build tool:** Maven

## 1. Introduction

The purpose of this assignment is to implement the Strategy design pattern in a delivery route optimization system. Different deliveries require different optimization algorithms. Urgent deliveries prioritize time, economy deliveries prioritize cost, eco deliveries prioritize emissions, and standard deliveries use balanced optimization.

## 2. Pattern Implementation

`RouteStrategy` defines the common interface for all route-selection algorithms.

`RoutePlanner` is the Context. It stores the current strategy and delegates route selection through the `selectRoute()` method.

The project contains five Concrete Strategies:

1. `FastestRouteStrategy`
2. `CheapestRouteStrategy`
3. `EcoFriendlyRouteStrategy`
4. `BalancedRouteStrategy`
5. `SafestRouteStrategy`

The strategies return `RouteDecision`, which contains the selected route, strategy name, score and explanation.

## 3. Route Restrictions

Before a route can participate in optimization, it must satisfy all hard constraints:

- the route must be available;
- cost must not exceed the request budget;
- delivery time must not exceed the deadline;
- emissions must not exceed the configured limit.

If no route satisfies the restrictions, `NoSuitableRouteException` is thrown.

## 4. Runtime Switching

`RoutePlanner.setStrategy()` changes the algorithm while the program is running. The same planner executes Fastest, Cheapest, Eco-Friendly, Balanced and Safest strategies without containing their algorithms.

## 5. Automatic Selection

`StrategySelector` maps delivery priorities to strategies using `EnumMap`.

- `URGENT` selects Fastest.
- `ECONOMY` selects Cheapest.
- `ECO` selects Eco-Friendly.
- `STANDARD` selects Balanced.

The selector chooses which strategy to use, while the strategy decides how the route is selected.

## 6. Strategy Comparison

`StrategyComparisonService` executes all available strategies for the same request. The comparison shows that different algorithms can choose different routes from identical input data.

Example:

```text
Fastest  -> Route B
Cheapest -> Route C
Eco      -> Route H
Balanced -> Route C
Safest   -> Route H 