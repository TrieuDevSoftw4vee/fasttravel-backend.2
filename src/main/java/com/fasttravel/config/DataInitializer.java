package com.fasttravel.config;

import com.fasttravel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner demoAccounts() {
        return args -> users.findAll().stream().filter(x -> x.getEmail().endsWith("@fasttravel.vn")).forEach(x -> {
            x.setPasswordHash(encoder.encode("123456"));
            users.save(x);
        });
    }
}
