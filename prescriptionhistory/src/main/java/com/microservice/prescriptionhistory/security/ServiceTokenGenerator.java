package com.microservice.prescriptionhistory.security;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ServiceTokenGenerator {

    private final JwtUtil jwtUtil;

    public ServiceTokenGenerator(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String generateServiceToken() {
        // generate a JWT similar to user tokens
        return io.jsonwebtoken.Jwts.builder()
                .setSubject("microservice-client")  // dummy username
                .claim("role", "SERVICE")          // role for microservice calls
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000)) // 1 hour
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "hvdSY75g3e27t11BIugQG78UE3JhvhVBY3@VT#t6YbGfWST2kn".getBytes()))
                .compact();
    }
}
