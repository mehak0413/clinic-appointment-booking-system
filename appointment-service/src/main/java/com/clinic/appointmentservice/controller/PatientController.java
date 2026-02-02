package com.clinic.appointmentservice.controller;

import com.clinic.appointmentservice.dto.PatientRequest;
import com.clinic.appointmentservice.dto.PatientResponse;
import com.clinic.appointmentservice.entity.Patient;
import com.clinic.appointmentservice.service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PatientResponse addPatient(@Valid @RequestBody PatientRequest patientRequest) {
        Patient patient = new Patient();
        patient.setName(patientRequest.getName());
        patient.setEmail(patientRequest.getEmail());
        patient.setPassword(patientRequest.getPassword());
        patient.setRole("PATIENT");

        Patient savedPatient = patientService.addPatient(patient);

        return new PatientResponse(savedPatient.getId(), savedPatient.getName(), savedPatient.getEmail());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PatientResponse> getAllPatients() {
        return patientService.getAllPatients().stream()
                .map(p -> new PatientResponse(p.getId(), p.getName(), p.getEmail()))
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isSelf(#id, authentication)")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(p -> ResponseEntity.ok(new PatientResponse(p.getId(), p.getName(), p.getEmail())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isSelf(#id, authentication)")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id,
            @RequestBody PatientRequest updatedPatient) {
        Patient patient = patientService.updatePatient(id, updatedPatient);
        return ResponseEntity.ok(new PatientResponse(patient.getId(), patient.getName(), patient.getEmail()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

}
