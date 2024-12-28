package com.ensa.CityScout.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
}