package com.ensa.CityScout.entity;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.type.AnyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private Utilisateurs utilisateur;

    @Lob
    @Column(name = "image_bytes", length = 1048576) // 1MB limit
    private byte[] imageBytes;


 

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PostType type;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "post_likes",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Utilisateurs> likes = new HashSet<>();

    // Update existing getters/setters
    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Utilisateurs getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(Utilisateurs utilisateur) {
		this.utilisateur = utilisateur;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public PostType getType() {
		return type;
	}

	public void setType(PostType type) {
		this.type = type;
	}

	public Set<Utilisateurs> getLikes() {
		return likes;
	}

	public void setLikes(Set<Utilisateurs> likes) {
		this.likes = likes;
	}
	  public void toggleLike(Utilisateurs user) {
	        if (likes.contains(user)) {
	            likes.remove(user);
	        } else {
	            likes.add(user);
	        }
	    }

}