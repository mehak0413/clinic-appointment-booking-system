package com.clinic.appointmentservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // Get the JWT token from Spring Security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof String jwt) {
            template.header("Authorization", "Bearer " + jwt);
            System.out.println("Forwarding JWT to Feign: " + jwt); // Optional debug
        }
    }
}