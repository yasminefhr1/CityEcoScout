package com.ensa.CityScout.repository;

import com.ensa.CityScout.entity.Place;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

	List<Place> findByCategoryIgnoreCase(String category); // Utilisation de Long pour l'ID

	List<Place> findByCountryIgnoreCase(String country);
}
