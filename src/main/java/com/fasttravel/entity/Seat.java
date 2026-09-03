package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"vehicle_id", "seat_number"}))
@Getter
@Setter
@NoArgsConstructor
public class Seat extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber;
    private Integer floor;
    private Integer rowIndex;
    private Integer columnIndex;
    private String seatType = "STANDARD";
    private boolean active = true;
}
