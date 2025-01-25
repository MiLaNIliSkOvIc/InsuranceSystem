package com.example.insurance_system.service;

import com.example.insurance_system.model.SecurityLog;
import com.example.insurance_system.repository.SecurityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityLogService {
    @Autowired
    private SecurityLogRepository securityLogRepository;

    public SecurityLog save(SecurityLog securityLog) {
        return securityLogRepository.save(securityLog);
    }
}