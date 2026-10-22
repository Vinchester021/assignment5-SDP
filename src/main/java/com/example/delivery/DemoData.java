package com.example.delivery;

import java.util.List;

public final class DemoData {

    private DemoData() {
    }

    public static List<Route> createRoutes() {
        Location warehouse = new Location(
                "Central Warehouse",
                51.1694,
                71.4491
        );

        Location university = new Location(
                "AITU Campus",
                51.0900,
                71.4180
        );

        return List.of(
                new Route(
                        "Route A",
                        warehouse,
                        university,
                        12.0,
                        22,
                        1500,
                        200,
                        3.8,
                        TrafficLevel.MEDIUM,
                        true,
                        8.5
                ),
                new Route(
                        "Route B",
                        warehouse,
                        university,
                        15.0,
                        20,
                        2200,
                        300,
                        5.2,
                        TrafficLevel.LOW,
                        true,
                        8.0
                ),
                new Route(
                        "Route C",
                        warehouse,
                        university,
                        10.0,
                        28,
                        900,
                        100,
                        2.2,
                        TrafficLevel.LOW,
                        true,
                        7.5
                ),
                new Route(
                        "Route D",
                        warehouse,
                        university,
                        8.5,
                        32,
                        1300,
                        150,
                        0.9,
                        TrafficLevel.LOW,
                        true,
                        9.0
                ),
                new Route(
                        "Route E",
                        warehouse,
                        university,
                        11.0,
                        24,
                        1600,
                        250,
                        3.0,
                        TrafficLevel.HIGH,
                        true,
                        8.8
                ),
                new Route(
                        "Route F",
                        warehouse,
                        university,
                        6.0,
                        15,
                        600,
                        0,
                        0.4,
                        TrafficLevel.LOW,
                        false,
                        9.5
                ),
                new Route(
                        "Route G",
                        warehouse,
                        university,
                        13.0,
                        25,
                        1100,
                        250,
                        1.6,
                        TrafficLevel.MEDIUM,
                        true,
                        8.2
                ),
                new Route(
                        "Route H",
                        warehouse,
                        university,
                        7.0,
                        45,
                        700,
                        1200,
                        0.6,
                        TrafficLevel.LOW,
                        true,
                        9.2
                )
        );
    }

    public static DeliveryRequest createStandardRequest() {
        return new DeliveryRequest(
                "DELIVERY-105",
                DeliveryPriority.STANDARD,
                10_000,
                60,
                false,
                6.0
        );
    }

    public static DeliveryRequest createUrgentRequest() {
        return new DeliveryRequest(
                "DELIVERY-URGENT",
                DeliveryPriority.URGENT,
                10_000,
                25,
                false,
                6.0
        );
    }

    public static DeliveryRequest createEconomyRequest() {
        return new DeliveryRequest(
                "DELIVERY-ECONOMY",
                DeliveryPriority.ECONOMY,
                2_000,
                60,
                false,
                6.0
        );
    }

    public static DeliveryRequest createEcoRequest() {
        return new DeliveryRequest(
                "DELIVERY-ECO",
                DeliveryPriority.ECO,
                10_000,
                60,
                true,
                2.0
        );
    }
}