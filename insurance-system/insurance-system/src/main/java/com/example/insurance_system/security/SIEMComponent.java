package com.example.insurance_system.security;

import com.example.insurance_system.model.SecurityLog;
import com.example.insurance_system.model.User;
import com.example.insurance_system.repository.SecurityLogRepository;
import com.example.insurance_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SIEMComponent {

    private static final Logger logger = Logger.getLogger(SIEMComponent.class.getName());
    private final SecurityLogRepository securityLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public SIEMComponent(SecurityLogRepository securityLogRepository, UserRepository userRepository) {
        this.securityLogRepository = securityLogRepository;
        this.userRepository = userRepository;
    }

    public void logSecurityEvent(String eventType, String details) {

        String logMessage = String.format("Time: %s, Event Type: %s, Details: %s",
                LocalDateTime.now(), eventType, details);
        logger.log(Level.WARNING, logMessage);


            SecurityLog securityLog = new SecurityLog();
            securityLog.setEventType(eventType);
            securityLog.setDescription(details);

            securityLogRepository.save(securityLog);
        }
    }

