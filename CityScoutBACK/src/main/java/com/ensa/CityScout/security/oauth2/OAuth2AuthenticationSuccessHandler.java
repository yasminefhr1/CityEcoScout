package com.ensa.CityScout.security.oauth2;

import com.ensa.CityScout.dto.AuthResponse;
import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.repository.UtilisateurRepository;
import com.ensa.CityScout.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        
        // Find or create user
        Utilisateurs user = utilisateurRepository.findByEmail(email)
                .orElseGet(() -> {
                    Utilisateurs newUser = new Utilisateurs();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setNom(oAuth2User.getAttribute("family_name"));
                    newUser.setPrenom(oAuth2User.getAttribute("given_name"));
                    newUser.setPays("Not Specified");
                    return utilisateurRepository.save(newUser);
                });

        // Generate JWT token
        String token = JwtUtil.generateToken(email);
        
        // Create response
        AuthResponse authResponse = new AuthResponse(token, user.getId());
        
        // Set response headers
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        // Write response
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }
}