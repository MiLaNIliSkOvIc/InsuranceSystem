package com.example.insurance_system.service;

import com.example.insurance_system.model.Session;
import com.example.insurance_system.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessionRepository;

    public Optional<Session> findByToken(String token) {
        return sessionRepository.findBySessionToken(token);
    }

    public Session save(Session session) {
        return sessionRepository.save(session);
    }
    public List<Session> findAll() {
        return sessionRepository.findAll();
    }
}