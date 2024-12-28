package com.ensa.CityScout.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
	
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Génère une clé sécurisée
    private static final long EXPIRATION_TIME = 86400000; // 1 jour en millisecondes

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

public static String validateTokenAndGetUsername(String token) {
    try {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    } catch (Exception e) {
        return null;
    }
}
}
