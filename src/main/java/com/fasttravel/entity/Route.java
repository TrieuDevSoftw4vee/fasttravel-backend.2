package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
public class Route extends BaseEntity {
    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @Column(nullable = false)
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_station_id")
    private Station originStation;
    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_station_id")
    private Station destinationStation;
    private Integer distanceKm;
    private Integer durationMinutes;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal basePrice;
    private boolean active = true;
}
