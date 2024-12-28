package com.ensa.CityScout.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ensa.CityScout.entity.Favorite;
import com.ensa.CityScout.entity.Place;
import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.service.FavoriteService;
import com.ensa.CityScout.service.PlaceService;
import com.ensa.CityScout.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlaceService placeService;

    // Méthode mise à jour
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleFavorite(@RequestBody FavoriteRequest request) {
        // Récupération des objets User et Place via leurs services respectifs
        Utilisateurs user = userService.getUserById(request.getUserId());
        Place place = placeService.getPlaceById(request.getPlaceId());

        if (user == null || place == null) {
            return ResponseEntity.badRequest().body("User or Place not found");
        }

        // Bascule l'état du favori pour ce lieu et cet utilisateur
        favoriteService.toggleFavorite(user, place);

        return ResponseEntity.ok("Favorite state toggled successfully.");
    }

    // Classe interne ou fichier séparé pour représenter la requête
    public static class FavoriteRequest {
        private Long userId;
        private Long placeId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getPlaceId() {
            return placeId;
        }

        public void setPlaceId(Long placeId) {
            this.placeId = placeId;
        }
    }
    
 // Méthode pour vérifier l'état du favori
    @GetMapping("/status")
    public ResponseEntity<Boolean> getFavoriteStatus(@RequestParam Long userId, @RequestParam Long placeId) {
        // Vérifier si l'utilisateur a favorisé le lieu
        boolean isFavorite = favoriteService.isFavorite(userId, placeId);

        return ResponseEntity.ok(isFavorite);
    }

    @GetMapping("/favorites/user")
    public ResponseEntity<List<Place>> getFavoritesByUserId(@RequestParam Long userId) {
        List<Place> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }



}
