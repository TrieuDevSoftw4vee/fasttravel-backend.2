package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {
    public enum Type {PAYMENT, REFUND}

    public enum Status {PENDING, SUCCESS, FAILED}

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @Column(nullable = false, unique = true, length = 50)
    private String transactionCode;
    private String gatewayTransactionId;
    private String gateway = "VNPAY";
    @Enumerated(EnumType.STRING)
    private Type type = Type.PAYMENT;
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;
    private String bankCode;
    private String responseCode;
    private String errorMessage;
    private LocalDateTime transactionTime;
}
