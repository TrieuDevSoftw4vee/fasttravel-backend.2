package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trip_reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"booking_id"}))
@Getter
@Setter
@NoArgsConstructor
public class TripReview extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;
}