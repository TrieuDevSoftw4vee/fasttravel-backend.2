package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket extends BaseEntity {
    public enum Status {PENDING, VALID, BOARDED, CANCELLED, REFUNDED}

    @Column(nullable = false, unique = true, length = 40)
    private String code;
    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_seat_id")
    private TripSeat tripSeat;
    @ManyToOne
    @JoinColumn(name = "pickup_station_id")
    private Station pickupStation;
    @ManyToOne
    @JoinColumn(name = "dropoff_station_id")
    private Station dropoffStation;
    @Column(nullable = false)
    private String passengerName;
    @Column(nullable = false)
    private String passengerPhone;
    private String passengerEmail;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal ticketPrice;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    private String qrPayload;
}
