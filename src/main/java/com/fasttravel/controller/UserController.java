package com.fasttravel.controller;

import com.fasttravel.dao.UserDAO;
import com.fasttravel.dto.ApiResponse;
import com.fasttravel.dto.UserDTO.UpdateProfileRequest;
import com.fasttravel.entity.User;
import com.fasttravel.exception.AppException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserDAO userDAO;

    @GetMapping("/profile")
    public ApiResponse<?> getProfile(Authentication auth) {
        Long userId = (Long) auth.getDetails();
        User user = userDAO.findById(userId)
                .orElseThrow(() -> AppException.notFound("Người dùng không tồn tại"));
        return ApiResponse.ok(user);
    }

    @PutMapping("/profile")
    public ApiResponse<?> updateProfile(Authentication auth, @Valid @RequestBody UpdateProfileRequest req) {
        Long userId = (Long) auth.getDetails();
        User user = userDAO.findById(userId)
                .orElseThrow(() -> AppException.notFound("Người dùng không tồn tại"));

        user.setFullName(req.fullName());
        user.setPhone(req.phone());
        user.setDateOfBirth(req.dateOfBirth());
        user.setGender(req.gender());
        user.setAddress(req.address());
        user.setAvatarUrl(req.avatarUrl());

        userDAO.save(user);
        return ApiResponse.ok("Cập nhật thông tin thành công", user);
    }
}