package com.ensa.CityScout.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ensa.CityScout.entity.Post;
import com.ensa.CityScout.entity.PostType;
import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.service.PostService;

import io.jsonwebtoken.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post request) {
        Post newPost = postService.createPost(request);
        return ResponseEntity.ok(newPost);
    }

    // Aimer ou annuler le "like" sur un post
    @PostMapping("/{postId}/like/{userId}")  // Changé pour utiliser path variables
    public ResponseEntity<Post> likePost(
        @PathVariable Long postId,
        @PathVariable Long userId  // Changé pour utiliser @PathVariable au lieu de @RequestBody
    ) {
        Post updatedPost = postService.likePost(postId, userId);
        return ResponseEntity.ok(updatedPost);
    }
}
