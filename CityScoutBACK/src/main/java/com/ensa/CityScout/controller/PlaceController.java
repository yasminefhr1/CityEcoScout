package com.ensa.CityScout.controller;

import com.ensa.CityScout.dto.PlaceResponse;
import com.ensa.CityScout.entity.Place;
import com.ensa.CityScout.service.PlaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@CrossOrigin(origins = "*") // Ajout pour permettre les requêtes cross-origin
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        List<Place> places = placeService.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    @GetMapping("/dto")
    public ResponseEntity<List<PlaceResponse>> getAllPlacesDto() {
        List<PlaceResponse> places = placeService.getAllPlacesDto();
        return ResponseEntity.ok(places);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getPlacesCount() {
        long count = placeService.countPlaces();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id) {
        Place place = placeService.getPlaceById(id);
        return ResponseEntity.ok(place);
    }
     
    @GetMapping("/category/{category}")
    public List<Place> getPlacesByCategory(@PathVariable String category) {
        return placeService.searchPlacesByCategory(category);
    }

    @GetMapping("/country/{country}")
    public List<Place> getPlacesByCountry(@PathVariable String country) {
        return placeService.searchPlacesByCountry(country);
    }
}
