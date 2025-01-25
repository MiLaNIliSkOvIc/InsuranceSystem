package com.example.insurance_system.auth.service;


import com.example.insurance_system.auth.model.LoginRequest;
import com.example.insurance_system.auth.model.RegistrationRequest;
import com.example.insurance_system.auth.model.VerifyRequest;
import com.example.insurance_system.model.Session;
import com.example.insurance_system.model.User;
import com.example.insurance_system.repository.SessionRepository;
import com.example.insurance_system.repository.UserRepository;
import com.example.insurance_system.security.JWT.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class AccessService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AuthService authService;
    private static final Logger logger = Logger.getLogger(AccessService.class.getName());
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public boolean validateToken(String token,int appId) {
        String username = jwtTokenUtil.extractUsername(token);
        String role = jwtTokenUtil.extractRole(token);
        if(appId == 2 && role.equals("client"))
            return false;
        return jwtTokenUtil.validateToken(token, username);
    }

    public void validateCredentials(LoginRequest loginRequest) throws Exception {
        authService.validateCredentials(loginRequest);
    }

    public void sendVerificationCode(String username) {
       authService.sendVerificationCode(username);
    }



    public void detectMaliciousActivity(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        logger.info("Request received from IP: " + ipAddress + ", User-Agent: " + userAgent);

        String transactionAmount = request.getParameter("amount");
        if (transactionAmount != null && Double.parseDouble(transactionAmount) > 10000) {
            logger.warning("Potential malicious request detected from IP: " + ipAddress);
            terminateSession();
        }
    }
    public String authenticateUser(VerifyRequest verifyRequestDTO) throws Exception {
        return authService.authenticateUser(verifyRequestDTO);
    }
    private void terminateSession() {
        logger.warning("User session terminated due to suspicious activity.");
    }

    public void registerUser(RegistrationRequest registrationRequest) throws Exception {

        authService.registerUser(registrationRequest.getUsername(),registrationRequest.getPassword(),registrationRequest.getEmail(), registrationRequest.getRole());
    }
    public String generateNewToken(String token)
    {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        String token2 = jwtTokenUtil.refreshToken(token);
        Session session = new Session();
        Optional<User> user = userRepository.findById(jwtTokenUtil.extractId(token));
        session.setUser(user.get());
        session.setSessionToken(token2);
        session.setExpiresAt(jwtTokenUtil.extractExpiration(token));
        sessionRepository.save(session);
        return token2;
    }
    @Transactional
    public boolean logout(String sessionToken) {

        Optional<Session> sessionOptional = sessionRepository.findBySessionToken(sessionToken);
        if (sessionOptional.isEmpty()) {
            return false;
        }
        Session session = sessionOptional.get();
        session.setActive(false);
        sessionRepository.save(session);

        return true;
    }
}
