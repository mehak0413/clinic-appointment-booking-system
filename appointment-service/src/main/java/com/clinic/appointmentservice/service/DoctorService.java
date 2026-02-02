package com.clinic.appointmentservice.service;

import com.clinic.appointmentservice.dto.DoctorRequest;
import com.clinic.appointmentservice.dto.DoctorResponse;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.exception.ResourceNotFoundException;
import com.clinic.appointmentservice.repository.DoctorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Doctor addDoctor(Doctor doctor) {
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        return doctorRepository.save(doctor);
    }

    public Doctor addDoctorEncoded(Doctor doctor) {
        return doctorRepository.save(doctor); // already encoded
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllByDeletedAtIsNull();
    }

    public List<Doctor> getDoctorsOnly() {
        return doctorRepository.findByRoleAndDeletedAtIsNull("DOCTOR");
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findByIdAndDeletedAtIsNull(id);
    }

    public Doctor updateDoctor(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialization(request.getSpecialization());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        doctor.setDeletedAt(LocalDateTime.now());
        doctorRepository.save(doctor);
    }

    public Optional<Doctor> findByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    public Doctor registerAdmin(Doctor admin, String rawPassword) {
        admin.setPassword(passwordEncoder.encode(rawPassword));
        return doctorRepository.save(admin);
    }

    public Doctor mapToEntity(DoctorRequest request) {
        Doctor doctor = new Doctor();
        doctor.setName(request.getName());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setPassword(request.getPassword());
        return doctor;
    }

    public DoctorResponse mapToResponse(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getName(),
                doctor.getEmail(),
                doctor.getSpecialization());
    }

}
