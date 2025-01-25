package com.example.insurance_system.controller;

import com.example.insurance_system.model.SecurityLog;
import com.example.insurance_system.service.SecurityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security-logs")
public class SecurityLogController {
    @Autowired
    private SecurityLogService securityLogService;

    @PostMapping
    public SecurityLog createLog(@RequestBody SecurityLog securityLog) {
        return securityLogService.save(securityLog);
    }
}