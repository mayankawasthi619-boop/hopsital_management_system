package com.hospital.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private Long id;
    private String name;
    private String email;
    private String role;
}
