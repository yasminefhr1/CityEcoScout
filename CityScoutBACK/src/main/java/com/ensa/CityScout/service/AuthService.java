package com.ensa.CityScout.service;

import com.ensa.CityScout.dto.*;
import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.repository.UtilisateurRepository;
import com.ensa.CityScout.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String signUp(SignUpRequest request) {
        logger.info("Tentative d'inscription pour l'utilisateur: {}", request.getUsername());

        // Validation des mots de passe
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.error("Les mots de passe ne correspondent pas pour {}", request.getUsername());
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        }

        // Vérification de l'unicité du username
        if (utilisateurRepository.existsByUsername(request.getUsername())) {
            logger.error("Username {} déjà utilisé", request.getUsername());
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé");
        }

        // Vérification de l'unicité de l'email
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            logger.error("Email {} déjà utilisé", request.getEmail());
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        // Création de l'utilisateur
        Utilisateurs user = new Utilisateurs();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setPays(request.getPays());

        // Traitement de la photo si présente
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            try {
                byte[] photoBytes = Base64.getDecoder().decode(request.getPhoto());
                user.setPhoto(photoBytes);
            } catch (IllegalArgumentException e) {
                logger.error("Erreur lors du décodage de la photo pour {}", request.getUsername(), e);
                throw new IllegalArgumentException("Format de photo invalide");
            }
        }

        utilisateurRepository.save(user);
        logger.info("Inscription réussie pour l'utilisateur: {}", request.getUsername());
        return "Inscription réussie";
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Tentative de connexion pour l'utilisateur: {}", request.getUsername());

        // Recherche de l'utilisateur
        Utilisateurs user = utilisateurRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé: {}", request.getUsername());
                    return new IllegalArgumentException("Identifiants invalides");
                });

        // Vérification du mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Mot de passe incorrect pour: {}", request.getUsername());
            throw new IllegalArgumentException("Identifiants invalides");
        }

        // Génération du token
        String token = JwtUtil.generateToken(user.getUsername());
        logger.info("Connexion réussie pour: {}", request.getUsername());
        
        return new AuthResponse(token, user.getId());
    }
    
}