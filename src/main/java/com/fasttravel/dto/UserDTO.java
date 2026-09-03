package com.fasttravel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public final class UserDTO {
    private UserDTO() {}

    public record UpdateProfileRequest(
            @NotBlank String fullName,
            @Pattern(regexp = "0[0-9]{9}") String phone,
            LocalDate dateOfBirth,
            String gender,
            String address,
            String avatarUrl
    ) {}
}