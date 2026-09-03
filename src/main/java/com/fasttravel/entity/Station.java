package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station extends BaseEntity {
    @ManyToOne(optional = false)
    @JoinColumn(name = "province_id")
    private Province province;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 255)
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private boolean active = true;
}
