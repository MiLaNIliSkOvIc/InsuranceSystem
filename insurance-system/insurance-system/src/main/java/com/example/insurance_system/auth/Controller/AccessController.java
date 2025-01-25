package com.example.insurance_system.auth.Controller;

import com.example.insurance_system.auth.model.AuthResponse;
import com.example.insurance_system.auth.model.LoginRequest;
import com.example.insurance_system.auth.model.RegistrationRequest;
import com.example.insurance_system.auth.model.VerifyRequest;
import com.example.insurance_system.auth.service.AccessService;
import com.example.insurance_system.security.SIEMComponent;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/access")
public class AccessController {

    private static final Logger logger = Logger.getLogger(AccessController.class.getName());

    @Autowired
    private AccessService accessService;

    @Autowired
    private SIEMComponent siemComponent;

    @GetMapping("/auth/validate-token/{appId}")
    public boolean validateToken(String token, @PathVariable int appId) {
        try {
//            String authorizationHeader = request.getHeader("Authorization");
//            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//                return ResponseEntity.ok(false);
//            }
//
//            String token = authorizationHeader.substring(7);
            boolean isValid = accessService.validateToken(token,appId);
            return isValid;

        } catch (Exception e) {
            logger.warning("Token validation failed: " + e.getMessage());
            return false;
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token format");
        }
        String sessionToken = token.substring(7);
        boolean logoutSuccess = accessService.logout(sessionToken);

        if (!logoutSuccess) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Session not found");
        }


        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) throws Exception {

        try {
            accessService.validateCredentials(loginRequest);
        } catch (Exception e) {
            siemComponent.logSecurityEvent("Failed Login", "Invalid credentials for user: " + loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        }


        accessService.sendVerificationCode(loginRequest.getUsername());
        return ResponseEntity.ok("Verification code sent to your email.");
    }



    @PostMapping("/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        try {
            accessService.registerUser(
                    registrationRequest
            );
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/auth/me/{appId}")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request,@PathVariable int appId) {
        String token = getJwtFromRequest(request);
        if(token!=null)
             if(validateToken(token,appId)) {
                 String token2 = accessService.generateNewToken(token);
                 return ResponseEntity.ok().body(token2);
             }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyRequest verifyRequest, HttpServletResponse response) {
        try {

            String token = accessService.authenticateUser(verifyRequest);
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Staviti na true za HTTPS
            cookie.setPath("/");
            cookie.setDomain("localhost"); // Omogućava deljenje kolačića između portova
            cookie.setMaxAge(3600); // Trajanje kolačića: 1 sat
            response.addCookie(cookie);

            return ResponseEntity.ok().body(new AuthResponse("Login successful", token));
        } catch (Exception e) {

            siemComponent.logSecurityEvent("Failed Login", "Invalid credentials for user: " + verifyRequest.getUsername());


            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Code");
        }
    }


    private void detectMaliciousActivity(HttpServletRequest request) {
        accessService.detectMaliciousActivity(request);
    }



}
