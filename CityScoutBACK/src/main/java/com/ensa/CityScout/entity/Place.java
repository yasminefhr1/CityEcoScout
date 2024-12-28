package com.ensa.CityScout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Entity
@Table(name = "places")
@Data
public class Place {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    @JsonProperty("place_name")
    private String name;

    private String address;
    private Double latitude;
    private Double longitude;
    private Long rating;

    @JsonProperty("photo_url")
    @Column(name = "photo_url", length = 512)
    private String photoUrl;

    @JsonProperty("flag_url")
    private String flagUrl;

    private String types;

    @JsonProperty("user_ratings_total")
    private Long userRatingsTotal;

    private String category;
    private String country;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Long getRating() {
		return rating;
	}
	public void setRating(Long rating) {
		this.rating = rating;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getFlagUrl() {
		return flagUrl;
	}
	public void setFlagUrl(String flagUrl) {
		this.flagUrl = flagUrl;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public Long getUserRatingsTotal() {
		return userRatingsTotal;
	}
	public void setUserRatingsTotal(Long userRatingsTotal) {
		this.userRatingsTotal = userRatingsTotal;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
    
}
