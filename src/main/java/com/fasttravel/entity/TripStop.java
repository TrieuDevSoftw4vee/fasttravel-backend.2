package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_stops")
@Getter
@Setter
@NoArgsConstructor
public class TripStop extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(nullable = false)
    private Integer stopOrder;

    private LocalDateTime estimatedTime;

    @Enumerated(EnumType.STRING)
    private StopType stopType = StopType.BOTH;

    public enum StopType { PICKUP, DROPOFF, BOTH }
}