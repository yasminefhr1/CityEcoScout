package com.ensa.CityScout.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ensa.CityScout.entity.*;
import com.ensa.CityScout.repository.*;
import com.ensa.CityScout.repository.FavoriteRepository;

@Service
public class FavoriteService {

	   @Autowired
	    private FavoriteRepository favoriteRepository;
	    
	    @Autowired
	    private PlaceRepository placeRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, PlaceRepository placeRepository) {
        this.favoriteRepository = favoriteRepository;
        this.placeRepository = placeRepository;
    }

    @Transactional
    public void toggleFavorite(Utilisateurs user, Place place) {
        if (user == null || place == null) {
            throw new IllegalArgumentException("User or Place not found");
        }

        var favorite = favoriteRepository.findByUserAndPlace(user, place);

        if (favorite != null) {  // Changed this condition
            System.out.println("Removing favorite for user " + user.getId() + " and place " + place.getId());
            favoriteRepository.deleteById(favorite.getId());
            place.setUserRatingsTotal(place.getUserRatingsTotal() - 1);
        } else {
            System.out.println("Adding favorite for user " + user.getId() + " and place " + place.getId());
            Favorite newFavorite = new Favorite();
            newFavorite.setUser(user);
            newFavorite.setPlace(place);
            newFavorite.setFavorite(true);
            favoriteRepository.save(newFavorite);

            place.setUserRatingsTotal(place.getUserRatingsTotal() + 1);
        }

        placeRepository.save(place);
    }

    public boolean isFavorite(Long userId, Long placeId) {
        // Vérifier si une entrée avec userId et placeId existe dans la table favoris avec is_favorite = true
        return favoriteRepository.existsByUserIdAndPlaceIdAndIsFavoriteTrue(userId, placeId);
    }

    public List<Place> getFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                       .map(Favorite::getPlace)
                       .collect(Collectors.toList());
    }

}
