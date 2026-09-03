package com.fasttravel.config;

import com.fasttravel.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwt;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filter(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        return http
                // Tắt CSRF vì backend sử dụng JWT
                .csrf(csrf -> csrf.disable())

                // Bật CORS và sử dụng bean corsConfigurationSource
                .cors(cors ->
                        cors.configurationSource(corsConfigurationSource)
                )

                // Không sử dụng session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Phân quyền API
                .authorizeHttpRequests(auth -> auth

                        // Cho phép request OPTIONS kiểm tra CORS
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // API đăng nhập, đăng ký
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // API công khai
                        .requestMatchers(
                                "/api/public/**"
                        ).permitAll()

                        // VNPay trả kết quả về backend
                        .requestMatchers(
                                "/api/payments/vnpay-return"
                        ).permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // API còn lại phải đăng nhập
                        .anyRequest().authenticated()
                )

                // Kiểm tra JWT trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwt,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.frontend-url:http://localhost:5173}")
            String frontendUrl
    ) {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép frontend Vite gọi backend
        configuration.setAllowedOrigins(
                List.of(frontendUrl)
        );

        // Các phương thức HTTP được phép
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Cho phép các header như Authorization, Content-Type
        configuration.setAllowedHeaders(
                List.of("*")
        );

        // Cho phép frontend đọc các header này
        configuration.setExposedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        // Cho phép gửi thông tin xác thực
        configuration.setAllowCredentials(true);

        // Lưu kết quả preflight trong 1 giờ
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Áp dụng CORS cho toàn bộ backend
        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}