package com.clinic.appointmentservice.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Maridul";  // You can change this
        String encoded = encoder.encode(rawPassword);
        System.out.println("Encoded password: " + encoded);
    }
}
