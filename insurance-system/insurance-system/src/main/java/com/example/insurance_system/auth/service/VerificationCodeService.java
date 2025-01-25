package com.example.insurance_system.auth.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class VerificationCodeService {

    public String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}
