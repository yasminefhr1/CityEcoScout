package com.ensa.CityScout.repository;

import com.ensa.CityScout.entity.Favorite;
import com.ensa.CityScout.entity.Place;
import com.ensa.CityScout.entity.Utilisateurs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
	Favorite findByUserAndPlace(Utilisateurs user, Place place);
	List<Favorite> findByUserId(Long userId);

	boolean existsByUserIdAndPlaceIdAndIsFavoriteTrue(Long userId, Long placeId);
}

	