package com.ensa.CityScout.controller;

import com.ensa.CityScout.dto.*;
import com.ensa.CityScout.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // À ajuster selon vos besoins de sécurité
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest request) {
        try {
            String response = authService.signUp(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription", e);
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Une erreur est survenue lors de l'inscription"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            logger.debug("Réponse login: {}", response);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion", e);
            return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Une erreur est survenue lors de la connexion"));
        }
    }
}