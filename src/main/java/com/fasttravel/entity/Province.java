package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@NoArgsConstructor
public class Province extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    private String code;
    private boolean active = true;
}
