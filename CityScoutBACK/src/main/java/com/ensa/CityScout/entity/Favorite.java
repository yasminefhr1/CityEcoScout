package com.ensa.CityScout.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite;

    @ManyToOne
    @JoinColumn(name = "place_id", referencedColumnName = "id", nullable = false)
    private Place place;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Utilisateurs user;

    // Getters and Setters



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Utilisateurs getUser() {
		return user;
	}

	public void setUser(Utilisateurs user) {
		this.user = user;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public boolean getisFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

    
}
