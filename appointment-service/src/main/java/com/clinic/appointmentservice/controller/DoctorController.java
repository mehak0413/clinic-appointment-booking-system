package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.DoctorRequest;
import com.clinic.appointmentservice.dto.DoctorResponse;
import com.clinic.appointmentservice.entity.Doctor;
import com.clinic.appointmentservice.service.DoctorService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DoctorResponse addDoctor(@Valid @RequestBody DoctorRequest doctorRequest) {
        Doctor doctor = new Doctor();
        doctor.setName(doctorRequest.getName());
        doctor.setEmail(doctorRequest.getEmail());
        doctor.setSpecialization(doctorRequest.getSpecialization());
        doctor.setPassword(doctorRequest.getPassword());
        doctor.setRole("DOCTOR");

        Doctor savedDoctor = doctorService.addDoctor(doctor);

        return new DoctorResponse(savedDoctor.getId(), savedDoctor.getName(),
                savedDoctor.getEmail(), savedDoctor.getSpecialization());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorResponse> getAllDoctors() {
        return doctorService.getAllDoctors().stream()
                .map(doc -> new DoctorResponse(doc.getId(), doc.getName(), doc.getEmail(), doc.getSpecialization()))
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isSelfDoctor(#id, authentication)")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(doc -> ResponseEntity
                        .ok(new DoctorResponse(doc.getId(), doc.getName(), doc.getEmail(), doc.getSpecialization())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isSelfDoctor(#id, authentication)")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable Long id, @RequestBody DoctorRequest request) {
        Doctor updated = doctorService.updateDoctor(id, request);

        DoctorResponse response = new DoctorResponse(updated.getId(), updated.getName(), updated.getEmail(),
                updated.getSpecialization());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
