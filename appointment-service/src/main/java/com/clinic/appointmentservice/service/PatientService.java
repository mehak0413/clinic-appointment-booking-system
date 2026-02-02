package com.clinic.appointmentservice.service;

import com.clinic.appointmentservice.dto.PatientRequest;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.exception.ResourceNotFoundException;
import com.clinic.appointmentservice.repository.PatientRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Patient addPatient(Patient patient) {
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        return patientRepository.save(patient);
    }

    public void addPatientEncoded(Patient patient) {
        patientRepository.save(patient); // no encoding
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAllByDeletedAtIsNull();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findByIdAndDeletedAtIsNull(id);
    }

    public Patient updatePatient(Long id, PatientRequest updatedPatient) {
        Patient patient = patientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        patient.setName(updatedPatient.getName());
        patient.setEmail(updatedPatient.getEmail());
        if (updatedPatient.getPassword() != null && !updatedPatient.getPassword().isBlank()) {
            patient.setPassword(passwordEncoder.encode(updatedPatient.getPassword()));
        }

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        patient.setDeletedAt(LocalDateTime.now());
        patientRepository.save(patient);
    }

    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
}
