package com.example.insurance_system.auth.service;


import com.example.insurance_system.auth.model.LoginRequest;
import com.example.insurance_system.auth.model.VerifyRequest;
import com.example.insurance_system.model.Session;
import com.example.insurance_system.model.User;
import com.example.insurance_system.repository.SessionRepository;
import com.example.insurance_system.repository.UserRepository;
import com.example.insurance_system.security.JWT.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private VerificationCodeService verificationCodeService;

    public AuthService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public void registerUser(String username, String password, String email,String role) throws Exception {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);
        newUser.setEmail(email);
        newUser.setVerificationCode(null);
        newUser.setRole(role);

        userRepository.save(newUser);
    }

    public void validateCredentials(LoginRequest loginRequest) throws Exception {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();


            if (userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
                return;
            }


            String expectedRole = "ROLE_" + loginRequest.getRole().toUpperCase();
            if (!userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(expectedRole))) {
                throw new Exception("User does not have the required role");
            }
            Optional<User> user = userRepository.findByUsername(loginRequest.getUsername());
            if(!user.get().getVerified())
                throw new Exception("User is not verified");

        } catch (AuthenticationException ex) {
            throw new Exception("Invalid username or password", ex);
        }
    }



    public void sendVerificationCode(String username) {
        String code = verificationCodeService.generateCode();
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setVerificationCode(code);
        userRepository.save(user);

        EmailService.sendEmail(user.getEmail(), "Your verification code", "Code: " + code);
    }

    public void verifyCode(VerifyRequest verifyRequest) throws Exception {
        User user = userRepository.findByUsername(verifyRequest.getUsername()).orElseThrow();
        if(!user.getVerificationCode().equals(verifyRequest.getCode()))
        {
            throw new Exception("Invalid code");
        }
    }

    public String authenticateUser(VerifyRequest verifyRequest) throws Exception {
        verifyCode(verifyRequest);

        Optional<User> user = userRepository.findByUsername(verifyRequest.getUsername());
        if (user.isEmpty()) {
            throw new Exception("User not found");
        }
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        String token = jwtTokenUtil.generateToken(verifyRequest.getUsername(), user.get().getRole(),user.get().getId());

        Session session = new Session();
        session.setUser(user.get());
        session.setSessionToken(token);
        session.setExpiresAt(jwtTokenUtil.extractExpiration(token));
        sessionRepository.save(session);
        return token;
    }

}
