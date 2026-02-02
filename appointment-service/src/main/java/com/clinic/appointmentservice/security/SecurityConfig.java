package com.clinic.appointmentservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final JwtFilter jwtFilter;
        private final RoleBasedAuthenticationSuccessHandler successHandler;

        public SecurityConfig(JwtFilter jwtFilter,
                        RoleBasedAuthenticationSuccessHandler successHandler) {
                this.jwtFilter = jwtFilter;
                this.successHandler = successHandler;
        }

        @Bean
        public static PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        // 1️⃣ API Security (JWT) — must be first to avoid conflicts
        @Bean
        @Order(1)
        public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
                http
                                .securityMatcher("/api/**") // Matches only /api/**
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/login", "/api/auth/register/**").permitAll()
                                                .requestMatchers("/api/appointments/book").hasRole("PATIENT")
                                                .requestMatchers("/api/appointments/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // 2️⃣ Web UI Security (form login + session) — last chain to catch all other
        // requests
        @Bean
        @Order(2)
        public SecurityFilterChain webUiSecurity(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/login", "/register/**", "/css/**", "/js/**",
                                                                "/favicon.png")
                                                .permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                                                .requestMatchers("/patient/**").hasRole("PATIENT")
                                                .requestMatchers("/appointments/**").hasAnyRole("PATIENT", "ADMIN","DOCTOR")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                .successHandler(successHandler) // redirects to role-based dashboard
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/")
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // session for
                                                                                                          // web UI
                                )
                                .csrf(csrf -> csrf.disable());

                return http.build();
        }
}
