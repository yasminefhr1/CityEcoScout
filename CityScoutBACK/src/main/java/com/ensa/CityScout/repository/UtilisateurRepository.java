package com.ensa.CityScout.repository;

import com.ensa.CityScout.entity.Utilisateurs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateurs, Long> {
    Optional<Utilisateurs> findByUsername(String username);
    Optional<Utilisateurs> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
