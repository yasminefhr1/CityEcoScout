package com.ensa.CityScout.service;

import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.repository.FavoriteRepository;
import com.ensa.CityScout.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    public Utilisateurs getUserById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
    }

    public Utilisateurs updateUser(Long id, Utilisateurs updatedUser) {
        Utilisateurs existingUser = getUserById(id);
        
        // Mise à jour des champs modifiables
        existingUser.setNom(updatedUser.getNom());
        existingUser.setPrenom(updatedUser.getPrenom());
        existingUser.setPays(updatedUser.getPays());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setUsername(updatedUser.getUsername());
        
        // Si une nouvelle photo est fournie
        if (updatedUser.getPhoto() != null) {
            existingUser.setPhoto(updatedUser.getPhoto());
        }

        return utilisateurRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new EntityNotFoundException("Utilisateur non trouvé");
        }
        
        // Supprimer d'abord tous les favoris de l'utilisateur
        favoriteRepository.findByUserId(id)
            .forEach(favorite -> favoriteRepository.delete(favorite));
        
        // Ensuite supprimer l'utilisateur
        utilisateurRepository.deleteById(id);
    }
}