package com.fasttravel.dto;
import jakarta.validation.constraints.*;import java.util.Map;
public final class AuthDTO{private AuthDTO(){}public record LoginRequest(@Email String email,@NotBlank String password){}public record RegisterRequest(@NotBlank String fullName,@Email String email,@Pattern(regexp="0[0-9]{9}") String phone,@Size(min=6) String password){}public record AuthResponse(String accessToken,Map<String,Object> user){}}
