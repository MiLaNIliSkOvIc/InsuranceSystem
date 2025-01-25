package com.example.insurance_system.repository;


import com.example.insurance_system.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findBySessionToken(String sessionToken);
    List<Session> findByUserId(Integer userId);
}
