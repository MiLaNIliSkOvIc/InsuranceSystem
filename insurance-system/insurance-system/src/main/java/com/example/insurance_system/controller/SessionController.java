package com.example.insurance_system.controller;

import com.example.insurance_system.model.Session;
import com.example.insurance_system.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    @Autowired
    private SessionService sessionService;
    @GetMapping
    public List<Session> getAllSessions() {
        return sessionService.findAll();
    }

    @PostMapping
    public Session createSession(@RequestBody Session session) {
        return sessionService.save(session);
    }
}
