package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
public class Trip extends BaseEntity {
    public enum Status {SCHEDULED, BOARDING, RUNNING, FINISHED, CANCELLED}

    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;
    @Column(nullable = false)
    private LocalDateTime departureTime;
    @Column(nullable = false)
    private LocalDateTime arrivalTime;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private Status status = Status.SCHEDULED;
    private String boardingNote;
}
