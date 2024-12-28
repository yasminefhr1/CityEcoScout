package com.ensa.CityScout.service;

import com.ensa.CityScout.dto.PlaceResponse;
import com.ensa.CityScout.entity.Place;
import com.ensa.CityScout.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }
    
    public List<PlaceResponse> getAllPlacesDto() {
        return placeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PlaceResponse convertToDto(Place place) {
        PlaceResponse dto = new PlaceResponse();
        dto.setId(String.valueOf(place.getId()));  // Convertir l'ID en String si nécessaire
        dto.setName(place.getName());
        dto.setAddress(place.getAddress());
        dto.setCategory(place.getCategory());
        dto.setLatitude(place.getLatitude());
        dto.setLongitude(place.getLongitude());
        dto.setImageUrl(place.getPhotoUrl());
        dto.setFavorite(false); // Remplacer par la vraie logique si nécessaire
        return dto;
    }
    
    public long countPlaces() {
        return placeRepository.count();
    }

    public Place getPlaceById(Long id) { // Utilisation de Long
        return placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place non trouvé avec l'ID : " + id));
    }
    
    public List<Place> searchPlacesByCategory(String category) {
        return placeRepository.findByCategoryIgnoreCase(category);
    }

    public List<Place> searchPlacesByCountry(String country) {
        return placeRepository.findByCountryIgnoreCase(country);
    }
}
