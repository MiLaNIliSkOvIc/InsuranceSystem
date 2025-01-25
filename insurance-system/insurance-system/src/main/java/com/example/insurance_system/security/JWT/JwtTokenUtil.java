package com.example.insurance_system.security.JWT;


import com.example.insurance_system.model.Session;
import com.example.insurance_system.repository.SessionRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtTokenUtil {

    private final String SECRET_KEY = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    @Autowired
    private SessionRepository sessionRepository;

    public String generateToken(String username, String role,int id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("id", id);
        claims.put("username",username);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);

        Optional<Session> sessionOptional = sessionRepository.findBySessionToken(token);
        if (!sessionOptional.isEmpty()) {
            Session session = sessionOptional.get();
            if(session.getActive()==false)
                return false;
        }

        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public int extractId(String token) {
        return extractClaim(token, claims -> claims.get("id", Integer.class));
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public GrantedAuthority extractAuthority(String token) {
        Claims claims = extractAllClaims(token);
        String role = (String) claims.get("role");
        return new SimpleGrantedAuthority(role);
    }

    public String refreshToken(String oldToken) {
        if (isTokenExpired(oldToken)) {
            throw new IllegalArgumentException("Token has expired");
        }

        Claims claims = extractAllClaims(oldToken);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);
        int id = claims.get("id", Integer.class);

        return generateToken(username, role, id);
    }
}
