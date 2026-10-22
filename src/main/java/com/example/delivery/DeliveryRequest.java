package com.example.delivery;

public record DeliveryRequest(
        String requestId,
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        boolean ecoPreferred,
        double maxCarbonEmissionKg
) {
    public DeliveryRequest {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException(
                    "Request ID cannot be empty"
            );
        }

        if (priority == null) {
            throw new IllegalArgumentException(
                    "Delivery priority is required"
            );
        }

        if (maxBudget <= 0) {
            throw new IllegalArgumentException(
                    "Maximum budget must be positive"
            );
        }

        if (maxDeliveryMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Maximum delivery time must be positive"
            );
        }

        if (maxCarbonEmissionKg <= 0) {
            throw new IllegalArgumentException(
                    "Maximum emission must be positive"
            );
        }
    }
}