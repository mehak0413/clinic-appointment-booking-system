package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.AdminRegistrationRequest;
import com.clinic.appointmentservice.dto.DoctorRequest;
import com.clinic.appointmentservice.dto.DoctorResponse;
import com.clinic.appointmentservice.dto.PatientRequest;
import com.clinic.appointmentservice.dto.PatientResponse;
import com.clinic.appointmentservice.dto.auth.LoginRequest;
import com.clinic.appointmentservice.dto.auth.LoginResponse;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.security.JwtUtil;
import com.clinic.appointmentservice.service.DoctorService;
import com.clinic.appointmentservice.service.PatientService;

import jakarta.validation.Valid;

//import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil,
            PatientService patientService, DoctorService doctorService,
            PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // Authenticate user
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Try to find the user (patient or doctor)
        var user = patientService.findByEmail(request.getEmail())
                .map(p -> new LoginResponse(jwtUtil.generateToken(p.getEmail(), p.getRole()), p.getRole()))
                .orElseGet(() -> doctorService.findByEmail(request.getEmail())
                        .map(d -> new LoginResponse(jwtUtil.generateToken(d.getEmail(), d.getRole()), d.getRole()))
                        .orElseThrow(() -> new RuntimeException("Invalid email or password")));

        return user;
    }

    // --- REGISTER PATIENT ---
    @PostMapping("/register/patient")
    public PatientResponse registerPatient(@Valid @RequestBody PatientRequest request) {
        Patient patient = new Patient();
        patient.setName(request.getName());
        patient.setEmail(request.getEmail());
        patient.setPassword(request.getPassword());
        patient.setRole("PATIENT");

        Patient savedPatient = patientService.addPatient(patient);

        return new PatientResponse(
                savedPatient.getId(),
                savedPatient.getName(),
                savedPatient.getEmail());
    }

    // --- REGISTER DOCTOR ---
    @PostMapping("/register/doctor")
    public DoctorResponse registerDoctor(@Valid @RequestBody DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPassword(request.getPassword());
        doctor.setRole("DOCTOR");

        Doctor savedDoctor = doctorService.addDoctor(doctor);

        return new DoctorResponse(
                savedDoctor.getId(),
                savedDoctor.getName(),
                savedDoctor.getEmail(),
                savedDoctor.getSpecialization());
    }

    // --- REGISTER ADMIN (Only existing admin can create another admin) ---
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorResponse registerAdmin(@Valid @RequestBody AdminRegistrationRequest request) {
        Doctor admin = new Doctor();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setSpecialization(request.getSpecialization());
        admin.setRole("ADMIN");

        Doctor savedAdmin = doctorService.registerAdmin(admin, request.getPassword());

        return new DoctorResponse(savedAdmin.getId(), savedAdmin.getName(), savedAdmin.getEmail(),
                savedAdmin.getSpecialization());
    }

}
