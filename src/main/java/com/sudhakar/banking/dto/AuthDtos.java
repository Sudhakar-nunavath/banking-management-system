package com.sudhakar.banking.dto;
import jakarta.validation.constraints.*;
public class AuthDtos {
 public record RegisterRequest(@NotBlank @Size(max=100) String fullName,@NotBlank @Email String email,@NotBlank @Size(min=8,max=72) String password) {}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password) {}
 public record AuthResponse(String token,String tokenType,Long userId,String fullName,String email,String role) {}
}
