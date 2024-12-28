package com.ensa.CityScout.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ensa.CityScout.entity.*;
import com.ensa.CityScout.repository.PostRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserService userService;

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUtilisateur(userService.getUserById(post.getUtilisateur().getId())); 
        return postRepository.save(post);
    }


    @Transactional
    public Post likePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
            
        Utilisateurs user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        post.toggleLike(user);
        return postRepository.save(post);
    }
}
