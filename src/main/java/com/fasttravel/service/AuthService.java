package com.fasttravel.service;import com.fasttravel.dto.AuthDTO.*;public interface AuthService{AuthResponse login(LoginRequest r);AuthResponse register(RegisterRequest r);}
