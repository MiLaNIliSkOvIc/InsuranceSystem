package com.example.insurance_system.security;

import com.example.insurance_system.auth.service.AccessService;
import com.example.insurance_system.model.Session;
import com.example.insurance_system.repository.SessionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class MaliciousRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(MaliciousRequestFilter.class);

    private final SIEMComponent siemComponent;
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    public MaliciousRequestFilter(SIEMComponent siemComponent) {
        this.siemComponent = siemComponent;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/payment/process")) {
            String amount = null;
            String requestURI = request.getRequestURI();
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> body = objectMapper.readValue(request.getInputStream(), Map.class);


                if (body.containsKey("amount")) {
                    Object amountValue = body.get("amount");
                    amount = amountValue.toString();


                }
            } catch (Exception e) {
                return;
            }

            if (isPotentiallyMalicious(amount)) {
                String warningMessage = String.format("Potentially malicious request detected: URI=%s Amount=%s", requestURI, amount);

                siemComponent.logSecurityEvent("Malicious Request", warningMessage);

                String sessionToken = null;
                String authorizationHeader = request.getHeader("Authorization");
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    sessionToken = authorizationHeader.substring(7);
                }
                if (sessionToken != null) {
                    Optional<Session> sessionOptional = sessionRepository.findBySessionToken(sessionToken);

                    Session session = sessionOptional.get();
                    session.setActive(false);
                    sessionRepository.save(session);

                }
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malicious request detected.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    private boolean isPotentiallyMalicious(String amount) {
        try {
            if (amount != null) {
                double value = Double.parseDouble(amount);
                return value > 1000000;
            }
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

}
