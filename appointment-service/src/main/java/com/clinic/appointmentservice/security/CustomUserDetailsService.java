package com.clinic.appointmentservice.security;

import com.clinic.appointmentservice.entity.CustomUserDetails;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.service.DoctorService;
import com.clinic.appointmentservice.service.PatientService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PatientService patientService;
    private final DoctorService doctorService;

    public CustomUserDetailsService(PatientService patientService,
                                    DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Attempting login for email: " + email);

        // Patient check
        Optional<Patient> optionalPatient = patientService.findByEmail(email);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            return buildUserDetails(patient.getId(), patient.getEmail(), patient.getPassword(), patient.getRole());
        }

        // Doctor/Admin check
        Optional<Doctor> optionalDoctor = doctorService.findByEmail(email);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            System.out.println("Doctor found: " + doctor.getEmail());
            System.out.println("Stored password (hashed): " + doctor.getPassword());
            System.out.println("Role: " + doctor.getRole());

            return buildUserDetails(doctor.getId(), doctor.getEmail(), doctor.getPassword(), doctor.getRole());
        }

        System.out.println("User not found with email: " + email);
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    private CustomUserDetails buildUserDetails(Long id, String email, String password, String role) {
        return new CustomUserDetails(
                id,
                email,
                password,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
        );
    }
}
