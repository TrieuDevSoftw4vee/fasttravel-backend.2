package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class Vehicle extends BaseEntity {
    public enum Type {SLEEPER, LIMOUSINE, SEATER}

    @Column(nullable = false, unique = true, length = 20)
    private String licensePlate;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private Type type;
    private Integer totalSeats;
    private Integer floors = 1;
    private String imageUrl;
    private boolean active = true;
}
