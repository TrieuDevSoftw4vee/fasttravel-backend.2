package com.fasttravel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class AdminTripDTO {
    private AdminTripDTO() {}

    public record SaveTripRequest(
            @NotNull Long routeId,
            @NotNull Long vehicleId,
            Long driverId,
            @NotNull LocalDateTime departureTime,
            @NotNull LocalDateTime arrivalTime,
            @NotNull @Positive BigDecimal price
    ) {}
}