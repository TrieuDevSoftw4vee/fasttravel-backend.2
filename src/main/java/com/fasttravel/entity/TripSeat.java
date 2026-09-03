package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_seats", uniqueConstraints = @UniqueConstraint(columnNames = {"trip_id", "seat_id"}))
@Getter
@Setter
@NoArgsConstructor
public class TripSeat extends BaseEntity {
    public enum Status {AVAILABLE, HELD, BOOKED, BLOCKED}

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private Trip trip;
    @ManyToOne(optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;
    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;
    private String holdToken;
    private LocalDateTime holdExpiresAt;
    @Version
    private Long version;
}
