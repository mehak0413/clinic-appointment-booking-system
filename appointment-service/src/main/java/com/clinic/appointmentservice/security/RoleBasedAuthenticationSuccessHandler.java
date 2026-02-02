package com.clinic.appointmentservice.security;

//import com.clinic.appointmentservice.security.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public RoleBasedAuthenticationSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Get email + role
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        // Generate JWT
        String jwt = jwtUtil.generateToken(email, role);

        // Save JWT in session
        HttpSession session = request.getSession(true);
        session.setAttribute("jwt", jwt);

        // Replace authentication credentials with JWT (for Feign interceptor)
        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        authentication.getPrincipal(),
                        jwt, // credentials now hold JWT
                        authentication.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // === Your existing role-based redirects ===
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            response.sendRedirect("/admin/dashboard");
            return;
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            response.sendRedirect("/doctor/dashboard");
            return;
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            response.sendRedirect("/patient/dashboard");
            return;
        }

        response.sendRedirect("/dashboard"); // fallback
    }
}
