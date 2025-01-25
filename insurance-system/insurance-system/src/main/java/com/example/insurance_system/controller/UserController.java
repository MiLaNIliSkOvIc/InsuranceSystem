package com.example.insurance_system.controller;

import com.example.insurance_system.model.User;
import com.example.insurance_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/block/{username}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<User> updateIsVerified(@PathVariable String username, @RequestParam boolean isVerified) {
        Optional<User> updatedUser = userService.updateIsVerified(username, isVerified);
        return updatedUser.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
