package com.fasttravel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    public enum Role {ADMIN, STAFF, DRIVER, CUSTOMER}

    public enum Status {ACTIVE, INACTIVE, LOCKED}

    @Column(nullable = false, length = 120)
    private String fullName;
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    @Column(nullable = false, unique = true, length = 20)
    private String phone;
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String avatarUrl;
}
