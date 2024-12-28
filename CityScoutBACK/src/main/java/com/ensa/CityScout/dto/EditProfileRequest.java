package com.ensa.CityScout.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileRequest {

    private String nom;
    private String prenom;
    private String pays;

    @Email
    private String email;

    private String username;
    private String password;

    private byte[] photo; // Photo encodée en Base64
}
