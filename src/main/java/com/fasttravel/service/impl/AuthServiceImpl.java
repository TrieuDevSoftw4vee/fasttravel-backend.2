package com.fasttravel.service.impl;

import com.fasttravel.dao.UserDAO;
import com.fasttravel.dto.AuthDTO.*;
import com.fasttravel.entity.User;
import com.fasttravel.exception.AppException;
import com.fasttravel.service.AuthService;
import com.fasttravel.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserDAO users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    private Map<String, Object> view(User u) {
        var m = new LinkedHashMap<String, Object>();
        m.put("id", u.getId());
        m.put("fullName", u.getFullName());
        m.put("email", u.getEmail());
        m.put("phone", u.getPhone());
        m.put("role", u.getRole());
        m.put("status", u.getStatus());
        m.put("dateOfBirth", u.getDateOfBirth());
        m.put("gender", u.getGender());
        m.put("address", u.getAddress());
        m.put("avatarUrl", u.getAvatarUrl());
        return m;
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email()).orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng"));
        if (u.getStatus() != User.Status.ACTIVE || !encoder.matches(r.password(), u.getPasswordHash()))
            throw new AppException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng hoặc tài khoản đã khóa");
        return new AuthResponse(jwt.create(u), view(u));
    }

    @Transactional
    public AuthResponse register(RegisterRequest r) {
        if (users.exists(r.email(), r.phone())) throw AppException.conflict("Email hoặc số điện thoại đã tồn tại");
        User u = new User();
        u.setFullName(r.fullName());
        u.setEmail(r.email().trim().toLowerCase());
        u.setPhone(r.phone());
        u.setPasswordHash(encoder.encode(r.password()));
        u = users.save(u);
        return new AuthResponse(jwt.create(u), view(u));
    }

}
