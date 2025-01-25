package com.example.insurance_system.service;

;
import com.example.insurance_system.model.User;
import com.example.insurance_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// User Service
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository ;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> updateIsVerified(String username, boolean isVerified) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setVerified(isVerified);
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
