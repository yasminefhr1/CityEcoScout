package com.ensa.CityScout.controller;

import com.ensa.CityScout.entity.Utilisateurs;
import com.ensa.CityScout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateurs> getUserProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Utilisateurs> updateProfile(@PathVariable Long id, @RequestBody Utilisateurs user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Compte supprimé avec succès");
    }
}