package com.ensa.CityScout.dto;

import lombok.Data;

@Data
public class PlaceResponse {
    private String id;
    private String name;
    private String address;
    private String category;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private boolean isFavorite;
}