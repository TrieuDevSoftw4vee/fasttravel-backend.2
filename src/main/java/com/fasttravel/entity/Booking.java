package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking extends BaseEntity {
    public enum Status {PENDING_PAYMENT, PAID, CANCELLED, EXPIRED, REFUNDED}

    @Column(nullable = false, unique = true, length = 30)
    private String code;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id")
    private Trip trip;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING_PAYMENT;
    private String paymentMethod;
    private LocalDateTime expiresAt;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}
