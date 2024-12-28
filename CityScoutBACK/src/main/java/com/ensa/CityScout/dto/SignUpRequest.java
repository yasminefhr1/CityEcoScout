package com.ensa.CityScout.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le pays est obligatoire")
    private String pays;

    @Email
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le username est obligatoire")
    private String username;

    @NotBlank(message = "Le password est obligatoire")
    private String password;

    @NotBlank(message = "Le confirmPassword est obligatoire")
    private String confirmPassword;

    private String photo; // Photo encodée en Base64
}
